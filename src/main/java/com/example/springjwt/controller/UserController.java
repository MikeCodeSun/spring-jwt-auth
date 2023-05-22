package com.example.springjwt.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springjwt.jwtToken.TokenUtil;
import com.example.springjwt.model.User;
import com.example.springjwt.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("user")
public class UserController {
  @Autowired 
  private UserRepository userRepository;
  @Autowired
  private AuthenticationManager authManager;
  @Autowired
  private TokenUtil tokenUtil;
  
  @PostMapping("login")
  public ResponseEntity<?> login(@Valid @RequestBody User user, BindingResult bResult) {
    if(bResult.hasErrors()){
      Map<String, String> errors = new HashMap<>();
      for(FieldError fe:bResult.getFieldErrors()){
        errors.put(fe.getField(), fe.getDefaultMessage());
      }
      return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    try {
      Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));
      System.out.println("isLogin: " + auth.isAuthenticated());
      User pricipalUser =(User)auth.getPrincipal();
      String token = tokenUtil.generateToken(pricipalUser);
      return ResponseEntity.ok().body(token);
    } catch (Exception e) {
      System.out.println("login ex: " + e);
      return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }
  }

  // @PostMapping("login")
  // public String rlogin(){
  //   return "login";
  // }

  // @PostMapping("register")
  // public String registert(){
  //   return "register";
  // }

  @PostMapping("register")
  public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult bResult) {
    if(bResult.hasErrors()){
      Map<String, String> errors = new HashMap<>();
      for(FieldError fe:bResult.getFieldErrors()){
        errors.put(fe.getField(), fe.getDefaultMessage());
      }
      return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
    String encodePassword = bc.encode(user.getPassword());
    user.setPassword(encodePassword);
    userRepository.save(user);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
