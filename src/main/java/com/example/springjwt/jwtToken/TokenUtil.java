package com.example.springjwt.jwtToken;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.springjwt.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtil {

  @Value("${app.jwt.secret}")
  private String SECRET;

  private static final long EXPIRATION_DURATION = 24 * 60 * 60 * 1000;

  public String generateToken(User user) {
    System.out.println("generate token");
    // System.out.println(user.getName());
    // System.out.println(user.getId());
    return Jwts
      .builder()
      .setIssuedAt(new Date())
      .setIssuer("Mike")
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DURATION))
      .setSubject(String.format("%s,%s", user.getId(), user.getName()))
      .signWith(SignatureAlgorithm.HS512, SECRET)
      .compact();
  }

  public boolean isTokenValid(String token) {
    System.out.println("check token is valid");
    try {
      Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
      System.out.println("valid true");
      return true;
    } catch (Exception e) {
      System.out.println("valid false");
      System.out.println(e);
      return false;
    }
  }

  public String getTokenSubject(String token) {
    System.out.println("get token subject");
    return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
  }

}
