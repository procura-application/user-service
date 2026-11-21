package com.eprocurement.user.security.service;


import com.eprocurement.user.users.enums.UserRoles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
	
	@Value("${application.security.jwt.secret.key}")
	private String jwtSecretKey;
	@Value("${application.security.jwt.secret.expiration}")
	private String jwtExpirationDate;
	
	
	public String buildToken(UserDetails userDetails, UserRoles role, String userId) {
		Map<String,Object> claimsMap = new HashMap<String, Object>();
		claimsMap.put("userId",userId);
		claimsMap.put("ROLE",role);
		return createJwtToken(userDetails, claimsMap, Long.parseLong(jwtExpirationDate));
	}
	
	public String createJwtToken(UserDetails userDetails, Map<String,Object> extraClaims,long expirationTime) {
		String username = userDetails.getUsername();
		return Jwts.builder()
				.claims()
				.add(extraClaims)
				.and()
				.subject(username)
				.expiration(new Date(System.currentTimeMillis()+expirationTime))
				.signWith(getSignInKey())
				.compact();
	}
	
	public boolean isValidToken(String jwtToken) {
		return extractExpirationdate(jwtToken).after(new Date(System.currentTimeMillis()));
	}
	
	public String extractRole(String jwtToken) {
		return extractClaim(jwtToken, t -> t.get("ROLE", String.class) ).toString();
	}
	public String extractUserName(String jwtToken) {
		return extractClaim(jwtToken, Claims::getSubject);
	}
	
	public Date extractExpirationdate(String jwtToken) {
		return extractClaim(jwtToken, Claims::getExpiration);
	}

	public String extractUserId(String jwtToken){
		return extractClaim(jwtToken,claims -> claims.get("userId",String.class));
	}
	public <T> T extractClaim(String jwtToken,Function<Claims, T> claimsResolvers) {
		final Claims claim = extractAllClaims(jwtToken);
		return claimsResolvers.apply(claim);
	}
	public Claims extractAllClaims(String jwtToken) {
		return Jwts.parser()
				.verifyWith((SecretKey) getSignInKey())
				.build()
				.parseSignedClaims(jwtToken)
				.getPayload();
	}
	

	
	public Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
