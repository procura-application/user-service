package com.eprocurement.user.config;

import com.eprocurement.user.security.service.JwtService;
import com.eprocurement.user.users.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String authorizationHeader = request.getHeader("Authorization");
		if(authorizationHeader== null || !authorizationHeader.startsWith("Bearer ")) 
		{
			filterChain.doFilter(request, response);
			return;
		}
			
		String jwtToken = authorizationHeader.substring(7);
		log.info("Jwt token : {}",jwtToken);
		if(jwtService.isValidToken(jwtToken)  && SecurityContextHolder.getContext().getAuthentication() == null) {
			String role = jwtService.extractRole(jwtToken);
			String username = jwtService.extractUserName(jwtToken);
			Claims claims = jwtService.extractAllClaims(jwtToken);
			String userId = jwtService.extractUserId(jwtToken);
			log.info("Parsed Role:{}",role);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			
			
			  SecurityContext context = SecurityContextHolder.createEmptyContext();
              UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                      userDetails,
                      userId,
                      userDetails.getAuthorities());
              authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              context.setAuthentication(authToken);
              SecurityContextHolder.setContext(context);
              
		}
		else {
			log.warn("The Provided JWT Token is not valid");
		}
		
		filterChain.doFilter(request, response);
		
	}

}
