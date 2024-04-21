package com.nellshark.backend.services;


import com.nellshark.backend.configs.properties.JwtProperties;
import com.nellshark.backend.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwtProperties;

  public String extractUsername(@NonNull String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(@NonNull String token, @NonNull Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(@NonNull User user) {
    return generateToken(new HashMap<>(), user);
  }

  public String generateToken(
      @NonNull Map<String, Object> extraClaims,
      @NonNull User user
  ) {
    return buildToken(extraClaims, user, jwtProperties.getExpiration());
  }

  public String generateRefreshToken(@NonNull User user) {
    return buildToken(new HashMap<>(), user, jwtProperties.getRefreshExpiration());
  }

  private String buildToken(
      @NonNull Map<String, Object> extraClaims,
      @NonNull User user,
      long expiration
  ) {
    extraClaims.put("userId", user.getId());
    extraClaims.put("role", user.getRole());
    return Jwts
        .builder()
        .claims(extraClaims)
        .subject(user.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey())
        .compact();
  }

  public boolean isTokenValid(@NonNull String token, @NonNull User user) {
    final String username = extractUsername(token);
    return (username.equals(user.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(@NonNull String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(@NonNull String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(@NonNull String token) {
    return Jwts.parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
