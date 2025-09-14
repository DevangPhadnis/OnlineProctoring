package com.example.OnlineProctoring.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

@Component
public class JWTUtils {

    @Value("${jwt.TokenExpiry}")
    private Integer tokenExpiryTime;

    @Value("${jwt.SecretKey}")
    private String SECRET_KEY = "";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUserName(String token) {
        Claims claims = extractClaims(token);
        if(claims != null) {
            return claims.getSubject();
        }
        else {
            return null;
        }
    }

    public Date extractExpiryDate(String token) {
        return extractClaims(token).getExpiration() != null ? extractClaims(token).getExpiration() : null;
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiryDate(token).before(new Date());
    }

    public String generateToken(String userName, String role, String sessionId) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("sessionId", sessionId);
        return createToken(userName, claims);
    }

    public String createToken(String subject, HashMap<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * tokenExpiryTime))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String extractUserRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }

    public String extractSessionId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("sessionId", String.class);
    }
}
