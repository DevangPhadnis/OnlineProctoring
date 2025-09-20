package com.example.OnlineProctoring.controller;

import com.example.OnlineProctoring.models.Response;
import com.example.OnlineProctoring.models.UserAuth;
import com.example.OnlineProctoring.models.UserDTO;
import com.example.OnlineProctoring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@RequestBody UserDTO userDTO) {
        logger.info("Inside UserRegistration method of UserController");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            Long flag = userService.addNewUser(userDTO);
            if(flag == 1L) {
                response.setData(null);
                response.setMessage("User Created Successfully");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                response.setData(0);
                response.setMessage("User Already Exists");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            logger.info("Outside UserRegistration method of UserController");
        } catch (Exception e) {
            logger.error("Error Found", e);
            response.setData(null);
            response.setMessage("Error in User Creation");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PostMapping("/login")
    private ResponseEntity<?> userLogin(@RequestBody UserAuth userAuth, HttpServletRequest httpServletRequest) {
        logger.info("Inside UserLogin method of UserController");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            String token = userService.verifyUser(userAuth, httpServletRequest);
            if(token != null) {
                response.setData(token);
                response.setMessage("User Logged in Successfully");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                response.setData(null);
                response.setMessage("User Login Failed!!! Please Check Username and Password");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info("Error Found", e);
            response.setData(null);
            response.setMessage("Error in User Verification and Login");
            response.setData("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }
}
