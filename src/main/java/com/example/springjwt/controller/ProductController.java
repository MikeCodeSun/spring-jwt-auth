package com.example.springjwt.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springjwt.model.Product;
import com.example.springjwt.repository.ProductRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("product")
public class ProductController {

  @Autowired
  private ProductRepository productRepository;

  @PostMapping
  public ResponseEntity<?> add(@Valid @RequestBody Product product, BindingResult bResult ) {
    if(bResult.hasErrors()){
      Map<String, String> errors = new HashMap<>();
      for(FieldError fe:bResult.getFieldErrors()){
        errors.put(fe.getField(), fe.getDefaultMessage());
      }
      return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    Product saveProduct =  productRepository.save(product);
    URI uri = URI.create("product" + saveProduct.getId());
    return ResponseEntity.created(uri).body(saveProduct);
  }
  
  @GetMapping
  public List<Product> all(){
    return productRepository.findAll();
  }
}
