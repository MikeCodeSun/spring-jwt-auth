package com.example.springjwt.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
 
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.springjwt.jwtToken.TokenUtil;
import com.example.springjwt.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
  Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
  @Autowired
  private TokenUtil tokenUtil;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if(!isHeader(request)){
      logger.debug("no header");
      System.out.println("not header");
      // response.getWriter().write("No header");
      
      filterChain.doFilter(request, response);
      return;
    }
    String token = getToken(request);
    boolean isValid = tokenUtil.isTokenValid(token);
    if(!isValid){
      logger.warn("token not valid");
      System.out.println("token not valid");
      // response.getWriter().write("token not valid");
      filterChain.doFilter(request, response);
      return;
    }
    logger.warn("token ok");
    System.out.println("token ok");
    setSecurityContext(token, request);
    filterChain.doFilter(request, response);
  }

  private boolean isHeader(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if(header == null || !header.startsWith("Bearer ")){
      return false;
    }
    return true;
  }

  private String getToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    return header.split(" ")[1];
  }

  // 0522 not understand fully
  private void setSecurityContext(String token, HttpServletRequest request) {
    UserDetails userDetails = getUser(token);
    System.out.println("security context");
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,null, null);
    System.out.println("security context token" );
    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    System.out.println("security context detail" );
    SecurityContextHolder.getContext().setAuthentication(auth);
    System.out.println("security context set" );
  }
  private User getUser(String token) {
    System.out.println("filter get user");
    User user = new User();
    String tokenSubject = tokenUtil.getTokenSubject(token);
    String[] subjects = tokenSubject.split(",");
    user.setId(Integer.parseInt(subjects[0]));
    user.setName(subjects[1]);
    return user;
  }
}
