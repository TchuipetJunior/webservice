package com.blog.app.webservice.security;

import com.blog.app.webservice.SpringApplicationContext;
import com.blog.app.webservice.service.impl.UserServiceImplementation;
import com.blog.app.webservice.shared.dto.UserDto;
import com.blog.app.webservice.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        String username = ((User) auth.getPrincipal()).getUsername();
        // Add the user's authorities/roles to the JWT
        UserServiceImplementation userService = (UserServiceImplementation) SpringApplicationContext.getBean("userServiceImplementation");
        UserDto userDto = userService.getUser(username);
        List<String> rolesList = new ArrayList<>();
        String role = userDto.getRole();
        rolesList.add(role);
        String roles = String.join(",", rolesList);

        String token =
                Jwts.builder()
                .claim("roles", roles) // Include roles in the JWT claims   Clames
                .subject(username)
                .expiration(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SecurityConstants.TOKEN_SECRET.getBytes()), SignatureAlgorithm.HS512)
                .compact();


        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("userId", userDto.getUserId());
    }
}
