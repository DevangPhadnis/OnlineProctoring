package com.example.OnlineProctoring.controller;

import com.example.OnlineProctoring.models.*;
import com.example.OnlineProctoring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

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
            } else if(flag == 2L) {
                response.setData(null);
                response.setMessage("User has already Registered via oauth/google. Please login using oauth/google.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
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
                if(token.equalsIgnoreCase("isGoogleType")) {
                    response.setData("isGoogleType");
                    response.setMessage("User has Registered via oauth/google. Please login using oauth/google.");
                    response.setStatus("2");
                    responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                else {
                    response.setData(token);
                    response.setMessage("User Logged in Successfully");
                    response.setStatus("1");
                    responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
            else {
                response.setData(null);
                response.setMessage("User Login Failed!!! Please Check Username and Password");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            logger.info("Outside UserLogin method of UserController");
        } catch (Exception e) {
            logger.info("Error Found", e);
            response.setData(null);
            response.setMessage("Error in User Verification and Login");
            response.setData("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }

    @PostMapping("/send-otp-forget-password")
    private ResponseEntity<?> sendOtpPassword(@RequestBody OtpDto otpDto, HttpServletRequest request) {
        logger.info("Inside ForgetPassword method of UserController");
        Response response = new Response();
        ResponseEntity<?> responseEntity = null;
        try {
            Long generatedOtp = userService.sendOtpPassword(otpDto, request);
            if(generatedOtp == 0L){
                response.setData("isManualTypeLogin");
                response.setMessage("You have registered with oauth/google. Please login using oauth/google.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if(generatedOtp == -1L) {
                response.setData("rateLimitError");
                response.setMessage("You have reached the current quota for generating OTP. Please try after some time.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
            } else if(generatedOtp == 1L) {
                response.setData("otpGeneratedSuccessfully");
                response.setMessage("OTP has been sent successfully to your registered email Id");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            }
            logger.info("Outside ForgetPassword method of UserController");
        } catch (UsernameNotFoundException e) {
            logger.info("Error Found", e);
            response.setData(null);
            response.setMessage("UserName is not valid. Please enter a valid username");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.info("Error Found", e);
            response.setData(null);
            response.setMessage("Error in generating OTP.");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @PostMapping("/verify-otp-forget-password")
    private ResponseEntity<?> verifyOtpPassword(@RequestBody OtpDto otpDto, HttpServletRequest request) {
        logger.info("Inside VerifyOtpAndPassword method of UserController");
        Response response = new Response();
        ResponseEntity<?> responseEntity = null;
        try {
            Long isOtpVerified = userService.verifyOtpPassword(otpDto, request);
            if(isOtpVerified == null) {
              throw new RuntimeException();
            } else if(isOtpVerified == 0L){
                response.setData("isManualTypeLogin");
                response.setMessage("You have registered with oauth/google. Please login using oauth/google.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if(isOtpVerified == -1L) {
                response.setData("rateLimitError");
                response.setMessage("You have reached the current quota for verify OTP. Please try after some time.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
            } else if(isOtpVerified == -2L) {
                response.setData("maxAttemptsCompletedError");
                response.setMessage("You have reached the max attempts for otp verification. Please try after some time.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
            } else if(isOtpVerified == -3L) {
                response.setData("invalidOtp");
                response.setMessage("Invalid Otp. Please send a valid Otp.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if(isOtpVerified == 1L) {
                response.setData("otpVerificationSuccessfully");
                response.setMessage("OTP verified successfully. Your new password has been sent to your registered mail Id.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            }
            logger.info("Outside VerifyOtpAndPassword method of UserController");
        } catch (UsernameNotFoundException e) {
            logger.info("Error Found", e);
            response.setData(null);
            response.setMessage("UserName is not valid. Please enter a valid username");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.info("Error Found", e);
            response.setData(null);
            response.setMessage("Error in OTP Verification.");
            response.setData("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping("/logged-in-user-profile")
    public ResponseEntity<?> loggedInUserProfile(Principal principal) {
        Response response = new Response();
        ResponseEntity<?> responseEntity = null;
        try {
            String userName = principal.getName();
            UserProfileDTO userProfileDTO = userService.fetchLoggedInUserProfile(userName);
            response.setData(userProfileDTO);
            response.setMessage("Successfully fetched logged in user's profile.");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error("Error Found", usernameNotFoundException);
            response.setData(null);
            response.setMessage("UserName is invalid please once logout and login again.");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error Found", e);
            response.setData(null);
            response.setMessage("Error in Fetching Logged in user profile");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
