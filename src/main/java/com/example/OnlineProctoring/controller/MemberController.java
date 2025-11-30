package com.example.OnlineProctoring.controller;

import com.example.OnlineProctoring.customExceptions.MemberDetailsNotFoundException;
import com.example.OnlineProctoring.models.MemberExamAttemptDTO;
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
            } else if(result == 0) {
                response.setData(null);
                response.setStatus("1");
                response.setMessage("Please select Valid Exam Details before proceeding for member registration");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if(result == -1) {
                response.setData(null);
                response.setStatus("1");
                response.setMessage("You have already registered for this exam. " +
                        "You can proceed with the next steps.");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
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

    @PostMapping("/start-exam")
    public ResponseEntity<?> startExam(@RequestBody MemberExamAttemptDTO memberExamAttemptDTO, Principal principal) {
        logger.info("Inside StartExam method of MemberController");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            String userName = principal.getName();
            MemberExamAttemptDTO memberExamAttemptDTO1 = memberService.startExam(memberExamAttemptDTO, userName);
            if(memberExamAttemptDTO1 != null) {
                if(memberExamAttemptDTO1.getAttemptId() != null) {
                    response.setData(memberExamAttemptDTO1);
                    response.setStatus("1");
                    response.setMessage("Best of Luck for the exam !!");
                } else {
                    response.setData(null);
                    response.setStatus("1");
                    response.setMessage("Sorry your Attempt is Expired. " +
                            "You can not move forward with the exam. Please contact Exam Administrator");
                }
            } else {
                response.setData(null);
                response.setStatus("1");
                response.setMessage("Sorry you can not start the exam currently. Please select a valid Exam");
            }
            responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            logger.info("Outside StartExam method of MemberController");
        } catch (MemberDetailsNotFoundException memberDetailsNotFoundException) {
            logger.error("Error Found", memberDetailsNotFoundException);
            response.setData(null);
            response.setMessage(memberDetailsNotFoundException.getMessage());
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error("Error Found", usernameNotFoundException);
            response.setData(null);
            response.setMessage("UserName is invalid please once logout and login again.");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error Found", e);
            response.setData(null);
            response.setStatus("1");
            response.setMessage("Error in starting an attempt. Please try after sometime");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
