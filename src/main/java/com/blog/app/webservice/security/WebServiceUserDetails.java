package com.blog.app.webservice.security;


import com.blog.app.webservice.io.entity.UserEntity;
import com.blog.app.webservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class WebServiceUserDetails implements UserDetailsService {

    public static final Logger logger = LoggerFactory.getLogger(WebServiceUserDetails.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String userName, password;
        List<GrantedAuthority> authorities;
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
        if (!userEntityOptional.isPresent()) {
            throw new UsernameNotFoundException("User details not found for the user with email : " + email);
        } else{
            UserEntity userEntity = userEntityOptional.get();

            userName = userEntity.getEmail();
            logger.info("getEncryptedPassword: "+ userEntity.getEncryptedPassword());
            password = userEntity.getEncryptedPassword();
            authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("read"));
        }
        return new User(userName,password,authorities);
    }

}
