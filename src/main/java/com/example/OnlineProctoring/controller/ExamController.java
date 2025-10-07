package com.example.OnlineProctoring.controller;

import com.example.OnlineProctoring.customExceptions.ExamQuestionEmptyException;
import com.example.OnlineProctoring.models.ExamDTO;
import com.example.OnlineProctoring.models.Response;
import com.example.OnlineProctoring.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {

    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);

    @Autowired
    private ExamService examService;

    @PostMapping(value = "/create-exam", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createExamFormData(@ModelAttribute ExamDTO examDTO, Principal principal) {
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

    @PostMapping(value = "/create-exam", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createExamJSON(@RequestBody ExamDTO examDTO, Principal principal) {
        logger.info("Inside createExamJSON method of ExamController");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            String userName = principal.getName();
            examService.createExam(examDTO, userName);
            response.setData(true);
            response.setMessage("Exam Created Successfully");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
            logger.info("Outside createExamJSON method of ExamController");
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

    @PostMapping("fetch-ongoing-exams")
    public ResponseEntity<?> fetchOngoingExams(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        logger.info("Inside FetchOngoingExam method of Exam Controller");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            List<ExamDTO> examDTOList = examService.fetchOngoingExams(pageNumber, pageSize);
            if(examDTOList != null && !examDTOList.isEmpty()) {
                response.setData(examDTOList);
                response.setMessage("Successfully Fetched Ongoing Exam Details.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
                logger.info("Outside FetchOngoingExam method of Exam Controller.");
            }
            else {
                response.setData(examDTOList);
                response.setMessage("No Ongoing Exams Found.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                logger.info("Outside FetchOngoingExam method of Exam Controller with," +
                        " No Ongoing Exam Details Found.");
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage("Error in Fetching Ongoing Exam Details");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PostMapping("fetch-upcoming-exams")
    public ResponseEntity<?> fetchUpcomingExams(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        logger.info("Inside FetchUpcomingExams method of ExamController");
        ResponseEntity<?> responseEntity = null;
        Response response = new Response();
        try {
            List<ExamDTO> examDTOList = examService.fetchUpcomingExams(pageNumber, pageSize);
            if(examDTOList != null && !examDTOList.isEmpty()) {
                response.setData(examDTOList);
                response.setMessage("Successfully Fetched Upcoming Exam Details.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
                logger.info("Outside FetchUpcomingExams method of Exam Controller.");
            } else {
                response.setData(examDTOList);
                response.setMessage("No Upcoming Exams Found.");
                response.setStatus("1");
                responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                logger.info("Outside FetchUpcomingExams method of Exam Controller with," +
                        " No Upcoming Exam Details Found.");
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage("Error in Fetching Upcoming Exam Details");
            response.setStatus("1");
            responseEntity = new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
