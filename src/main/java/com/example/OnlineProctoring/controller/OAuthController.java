package com.example.OnlineProctoring.controller;

import com.example.OnlineProctoring.models.Response;
import com.example.OnlineProctoring.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequestMapping("/auth/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> googleOAuthLogin(@RequestBody HashMap<String, String> tokenDetails, HttpServletRequest request) {
        String token = oAuthService.oAuthLogin(tokenDetails.getOrDefault("tokenId", null), request);
        if(token == null) {
            Response response = new Response();
            response.setData(null);
            response.setMessage("Input Output Exception");
            response.setStatus("0");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else if (token.equalsIgnoreCase("0")) {
            Response response = new Response();
            response.setData(null);
            response.setMessage("Token is Invalid");
            response.setStatus("0");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else if (token.equalsIgnoreCase("-1")) {
            Response response = new Response();
            response.setData(null);
            response.setMessage("Token Not Found");
            response.setStatus("0");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else if(token.equalsIgnoreCase("isManualTypeLogin")) {
            Response response = new Response();
            response.setData("isManualTypeLogin");
            response.setMessage("Selected mail Id is already registered under manual Login. Please Enter username and password to login.");
            response.setStatus("1");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        else {
            Response response = new Response();
            response.setData(token);
            response.setMessage("Login Successfully");
            response.setStatus("1");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}