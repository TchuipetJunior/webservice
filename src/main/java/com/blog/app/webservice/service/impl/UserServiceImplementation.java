package com.blog.app.webservice.service.impl;

import com.blog.app.webservice.io.entity.UserEntity;
import com.blog.app.webservice.repositories.UserRepository;
import com.blog.app.webservice.service.UserService;
import com.blog.app.webservice.shared.Utils;
import com.blog.app.webservice.shared.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImplementation implements UserService{

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImplementation.class);

    @Autowired
    Utils utils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserDto createUser(UserDto user) {

        Optional<UserEntity> userEntityOptional= userRepository.findByEmail(user.getEmail());
        if (userEntityOptional.isPresent()) {
            throw  new RuntimeException("User already exists");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        String publicUserId = utils.generateUssrId(30);

        String encryptedPassword = "{bcrypt}"+bCryptPasswordEncoder.encode(user.getPassword());
        logger.info("encryptedPassword "+ encryptedPassword);

        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setUserId(publicUserId);

        UserEntity storedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

}
