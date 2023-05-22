package com.example.springjwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.springjwt.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
  
  @Query("SELECT u FROM User u WHERE u.name=?1")
  Optional<User> findUserbyName(String name);
}
