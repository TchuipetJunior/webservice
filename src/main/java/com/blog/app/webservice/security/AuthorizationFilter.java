package com.blog.app.webservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationFilter extends BasicAuthenticationFilter {
//    private final String HEADER_STRING = SecurityConstants.HEADER_STRING;
//    private final String TOKEN_PREFIX = SecurityConstants.TOKEN_PREFIX;
//    private final Key TOKEN_SECRET = SecurityConstants.TOKEN_SECRET;
    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            Claims claims =Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SecurityConstants.TOKEN_SECRET.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String user = claims.getSubject();
            String roles = claims.get("roles", String.class);

            if (user != null) {
                List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
        }
        return null;
    }

    private Key getSignKey() {
        // byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        // return Keys.hmacShaKeyFor(keyBytes);
        return Jwts.SIG.HS256.key().build();
    }
}
