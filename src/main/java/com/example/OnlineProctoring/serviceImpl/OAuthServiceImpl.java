package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.models.Session;
import com.example.OnlineProctoring.models.UserAuth;
import com.example.OnlineProctoring.models.UserDetails;
import com.example.OnlineProctoring.repository.SessionRepository;
import com.example.OnlineProctoring.repository.UserDetailsRepository;
import com.example.OnlineProctoring.repository.UserRepository;
import com.example.OnlineProctoring.service.OAuthService;
import com.example.OnlineProctoring.utils.JWTUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OAuthServiceImpl implements OAuthService {

    private static final Logger logger = LoggerFactory.getLogger(OAuthServiceImpl.class);

    @Value("${oauth.clientId}")
    private String clientId;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private JWTUtils jwtUtils;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String oAuthLogin(String tokenId, HttpServletRequest request) {
        if(tokenId != null && !tokenId.equalsIgnoreCase("")) {
            try {
                GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport()
                        , GsonFactory.getDefaultInstance()).setAudience(Collections.singletonList(clientId)).build();

                GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(tokenId);
                if(googleIdToken != null) {
                    GoogleIdToken.Payload payload = googleIdToken.getPayload();
                    String email = payload.getEmail();
                    String name = (String) payload.get("name");

                    UserAuth userAuth = userRepository.findByEmail(email);
                    String role = userAuth != null ? userAuth.getRole() : null;
                    String userName = userAuth != null ? userAuth.getUserName() : null;
                    if(userAuth == null) {
                        UserAuth userAuth1 = new UserAuth();
                        userAuth1.setEmail(email);
                        userName = generateUserName(email);
                        userAuth1.setUserName(userName);
                        userAuth1.setPassword(encoder.encode("GOOGLE_USER"));
                        userAuth1.setLoginType("GOOGLE");
                        userAuth1.setRole("USER");
                        role = "USER";
                        userAuth1.setCreatedDate(LocalDateTime.now());
                        userRepository.save(userAuth1);
                        UserDetails userDetails = new UserDetails();
                        userDetails.setUserAuth(userAuth1);
                        userDetails.setFullName(name);
                        userDetails.setCreatedDate(LocalDateTime.now());
                        userDetailsRepository.save(userDetails);
                    }

                    String fingerPrintRequestDetails = generateFingerPrintHash(request);
                    Optional<Session> sessionDetails =  sessionRepository.findByRequestDetails(fingerPrintRequestDetails);
                    String sessionId;
                    if(sessionDetails.isPresent()) {
                        if(!sessionDetails.get().getUserName().equals(userName)) {
                            sessionRepository.deleteById(sessionDetails.get().getId());
                            sessionId = UUID.randomUUID().toString();
                            Session session = new Session();

                            session.setSessionId(sessionId);
                            session.setCreatedDate(LocalDateTime.now());
                            session.setStatus(1);
                            session.setUserName(userName);
                            session.setRequestDetails(fingerPrintRequestDetails);
                            sessionRepository.save(session);
                        }
                        else {
                            sessionId = sessionDetails.get().getSessionId();
                        }
                    }
                    else {
                        sessionId = UUID.randomUUID().toString();
                        Session session = new Session();

                        session.setSessionId(sessionId);
                        session.setCreatedDate(LocalDateTime.now());
                        session.setStatus(1);
                        session.setUserName(userName);
                        session.setRequestDetails(fingerPrintRequestDetails);
                        sessionRepository.save(session);
                    }
                    String jwtToken = jwtUtils.generateToken(userName, role, sessionId);
                    return jwtToken;
                }
                else {
                    logger.error("Invalid Token Id");
                    return "0";
                }
            } catch (GeneralSecurityException | IOException e) {
                logger.error("Error Found", e);
                return null;
            }
        }
        else {
            logger.error("Token Not Found");
            return "-1";
        }
    }

    public String generateUserName(String email) {
        String prefixEmail = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;

        String prefix = null;
        if(prefixEmail.length() >= 4) {
            prefix = prefixEmail.substring(0, 4);
        }
        else {
            prefix = prefixEmail;
        }

        String randomUUID = UUID.randomUUID().toString().substring(0,4);
        int year = Year.now().getValue();
        return prefix.toUpperCase() + randomUUID.toUpperCase() + year;
    }

    private String generateFingerPrintHash(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");
        if(userAgent == null) {
            userAgent = "unknown";
        }

        String normalizedUserAgent = userAgent.toLowerCase().replaceAll("\\s+", " ").trim();
        String combined = ipAddress + "_" + normalizedUserAgent;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
