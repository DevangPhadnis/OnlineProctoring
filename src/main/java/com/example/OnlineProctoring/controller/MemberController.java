package com.example.OnlineProctoring.controller;

import com.example.OnlineProctoring.models.MemberRegistrationDTO;
import com.example.OnlineProctoring.models.Response;
import com.example.OnlineProctoring.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<?> memberRegistration(@RequestBody MemberRegistrationDTO memberRegistrationDTO, Principal principal) {
        logger.info("Inside MemberRegistration method of MemberController");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            String userName = principal.getName();
            Integer result = memberService.memberRegistration(memberRegistrationDTO, userName);
            if(result == 1) {
                response.setData(null);
                response.setStatus("1");
                response.setMessage("Member Registered Successfully for the selected Exam");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setStatus("1");
                response.setMessage("Please select Valid Exam Details before proceeding for member registration");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            logger.info("Outside MemberRegistration method of MemberController");
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error("Error Found", usernameNotFoundException);
            response.setData(null);
            response.setMessage("UserName is invalid please once logout and login again.");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setData(null);
            response.setStatus("1");
            response.setMessage("Error in Member Registration. Please try after sometime");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
