package com.blog.app.webservice.service;

import com.blog.app.webservice.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService  {
    UserDto createUser(UserDto user);
    UserDto getUser(String email);
}
