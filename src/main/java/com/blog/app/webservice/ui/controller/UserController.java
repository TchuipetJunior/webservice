package com.blog.app.webservice.ui.controller;

import com.blog.app.webservice.service.UserService;
import com.blog.app.webservice.shared.dto.ResponseDto;
import com.blog.app.webservice.shared.dto.UserDto;
import com.blog.app.webservice.ui.model.request.UserDetailsRequestModel;
import com.blog.app.webservice.ui.model.response.UserRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasAnyRole('USER')")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public String getUser() {
        return "get user was called";
    }

    @PostMapping("/create")
    public ResponseEntity<UserRest> creatUser(@RequestBody UserDetailsRequestModel userDetails){
        logger.info("About to register user: "+ userDetails);
        UserRest returnValue = new UserRest();
        try {

            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userDetails, userDto);
            UserDto createdUser = userService.createUser(userDto);
            BeanUtils.copyProperties(createdUser, returnValue);
            logger.info("Successfully register user: " + returnValue);

        }catch (Exception e){

            returnValue.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            returnValue.setStatusMsg(e.getMessage());

           return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(returnValue);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER')")  // Only users with ROLE_USER can access
    public String updateUser(){
        return "update user was called";
    }

    @DeleteMapping
    public String deleteUser(){
        return "delete user was called";
    }
}
