package com.example.OnlineProctoring.controller;

import com.example.OnlineProctoring.customExceptions.ExamQuestionEmptyException;
import com.example.OnlineProctoring.models.ExamDTO;
import com.example.OnlineProctoring.models.Response;
import com.example.OnlineProctoring.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/exam")
public class ExamController {

    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);

    @Autowired
    private ExamService examService;

    @PostMapping("/create-exam")
    public ResponseEntity<?> createExam(@RequestBody ExamDTO examDTO, Principal principal) {
        logger.info("Inside CreateExam method of ExamController");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            String userName = principal.getName();
            examService.createExam(examDTO, userName);
            response.setData(true);
            response.setMessage("Exam Created Successfully");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            logger.info("Outside CreateExam method of ExamController");
        } catch (ExamQuestionEmptyException examQuestionEmptyException) {
            logger.error("Error Found:", examQuestionEmptyException);
            response.setData(null);
            response.setMessage(examQuestionEmptyException.getMessage());
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error("Error Found", usernameNotFoundException);
            response.setData(null);
            response.setMessage("UserName is invalid please once logout and login again.");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage("Error in creating an exam. Please check after sometime");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
