package com.shipkart.Shipkart.JwtUtil;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "fYtV6k9rGZJzT2ZcGm8Jj97LQ5dI5t9Z9aUgF8hR7lFkcQeR9tKpLuSh2oBq8Ex3"; // Change
																													// this
																													// in
																													// production
	private static final long EXPIRATION_TIME = 2 * 60 * 1000; // 30 minutes
	private Set<String> blacklistedTokens = new HashSet<>();

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

	public String generateToken(String username) {
		return Jwts.builder().subject(username).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(getSigningKey()).compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Check if the token is expired
	public boolean isTokenExpired(String token) {
		try {
			Date expiration = Jwts.parser().verifyWith(getSigningKey()) // ✅ Updated for JJWT 0.12.0
					.build().parseSignedClaims(token).getPayload().getExpiration();
			return expiration.before(new Date()); // If expiration is before current time, token is expired
		} catch (JwtException e) {
			return true;
			
		}
	}

	public void blacklistToken(String token) {
		blacklistedTokens.add(token);
	}

	public boolean isTokenBlacklisted(String token) {
		return blacklistedTokens.contains(token);
	}
}
