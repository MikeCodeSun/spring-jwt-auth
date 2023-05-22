package com.example.springjwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.example.springjwt.filter.JwtTokenFilter;
import com.example.springjwt.repository.UserRepository;

@Component
public class CustomWebSecurity {

  @Autowired
  private JwtTokenFilter jwtTokenFilter;

  @Autowired
  private UserRepository userRepository;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
    auth.userDetailsService( name -> userRepository.findUserbyName(name).orElseThrow(()->{
      throw new UsernameNotFoundException(name + "not Found");
    }));
    return auth.build();
  }
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(cf-> cf.disable())
      .cors(cs -> cs.disable())
      .authorizeHttpRequests(ahr -> ahr
        .requestMatchers("user/login", "user/register")
        .permitAll()
        .anyRequest()
        .authenticated())
      .sessionManagement(sm -> sm
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      
      .exceptionHandling(eh -> eh
        .authenticationEntryPoint((req, res, ex) -> {
          System.out.println("exception Handling");
          System.out.println(ex.getMessage());
          // res.getWriter().write(ex.getMessage());
          return;
      }))
      .authenticationManager(authManager(http))
      .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
      

      return http.build();
  }
}
