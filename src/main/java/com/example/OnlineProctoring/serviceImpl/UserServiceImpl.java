package com.example.OnlineProctoring.serviceImpl;

import com.example.OnlineProctoring.models.Session;
import com.example.OnlineProctoring.models.UserAuth;
import com.example.OnlineProctoring.models.UserDTO;
import com.example.OnlineProctoring.models.UserDetails;
import com.example.OnlineProctoring.repository.SessionRepository;
import com.example.OnlineProctoring.repository.UserDetailsRepository;
import com.example.OnlineProctoring.repository.UserRepository;
import com.example.OnlineProctoring.service.EmailService;
import com.example.OnlineProctoring.service.UserService;
import com.example.OnlineProctoring.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGIT_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String ALL_CHARS = LOWERCASE_CHARS + UPPERCASE_CHARS + DIGIT_CHARS + SPECIAL_CHARS;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Override
    public Long addNewUser(UserDTO userDTO) throws RuntimeException {
        logger.info("Inside AddNewUser method of UserServiceImpl");
        Long flag = 0L;
        try {
            UserAuth userAuth = userRepository.findByEmail(userDTO.getEmail());
            if(userAuth == null) {
                String userName = userNameCreation(userDTO.getFullName());
                UserAuth userAuth1 = new UserAuth();
                userAuth1.setUserName(userName);
                userAuth1.setEmail(userDTO.getEmail());
                userAuth1.setRole("USER");
                String password = generateStrongPassword(12);
                String encodedPassword = bCryptPasswordEncoder.encode(password);
                userAuth1.setPassword(encodedPassword);
                userAuth1.setLoginType("MANUAL");
                userAuth1.setCreatedDate(LocalDateTime.now());
                userRepository.save(userAuth1);

                UserDetails userDetails = new UserDetails();
                userDetails.setFullName(userDTO.getFullName());
                userDetails.setGender(userDTO.getGender());
                userDetails.setMobileNumber(userDTO.getMobileNumber());
                userDetails.setCreatedDate(LocalDateTime.now());
                userDetails.setUserAuth(userAuth1);

                userDetailsRepository.save(userDetails);

                String name = userDTO.getFullName();
                String to = userDTO.getEmail();
                String subject = "User Registration Completed Successfully";
                String body = "Dear " + name + ",\n\n" +
                        "Your account has been successfully created on EMS.\n\n" +
                        "Login Credentials\n" +
                        "Username: " + userName + "\n" +
                        "Password: " + password + "\n" +
                        "Please Change the default password soon" + "\n\n" +
                        "Thanks and Regards.\n";

                emailService.sendEmailWithoutAttachment(to, subject, body);

                logger.info("Outside AddNewUser method  of UserServiceImpl");
                return 1L;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public String verifyUser(UserAuth userAuth, HttpServletRequest request) throws Exception {
        logger.info("Inside VerifyUser method of UserServiceImpl");
        try {
            UserAuth currentUserAuth = null;
            if(userAuth.getUserName().contains("@")) {
                currentUserAuth = userRepository.findByEmail(userAuth.getUserName());
            }
            String userName = currentUserAuth != null ? currentUserAuth.getUserName() : userAuth.getUserName();
            Authentication authentication = authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(userName, userAuth.getPassword()));
            if(authentication.isAuthenticated()) {
                currentUserAuth = currentUserAuth != null ? currentUserAuth : userRepository.findByUserName(userName);
                if(currentUserAuth != null) {
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

                    return jwtUtils.generateToken(userName, currentUserAuth.getRole(), sessionId);
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String userNameCreation(String fullName) {
        String prefix = fullName.length() >= 4 ? fullName.substring(0, 4) :  fullName;
        String randomUUID = UUID.randomUUID().toString().substring(0,4);
        int year = Year.now().getValue();
        return prefix.toUpperCase() + randomUUID.toUpperCase() + year;
    }

    public static String generateStrongPassword(int length) {
        if (length < 8) { // Recommend a minimum length for strong passwords
            throw new IllegalArgumentException("Password length should be at least 8 characters.");
        }

        SecureRandom random = new SecureRandom();
        StringBuilder passwordBuilder = new StringBuilder();

        // Ensure at least one of each character type
        passwordBuilder.append(LOWERCASE_CHARS.charAt(random.nextInt(LOWERCASE_CHARS.length())));
        passwordBuilder.append(UPPERCASE_CHARS.charAt(random.nextInt(UPPERCASE_CHARS.length())));
        passwordBuilder.append(DIGIT_CHARS.charAt(random.nextInt(DIGIT_CHARS.length())));
        passwordBuilder.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill the rest of the password with random characters from all sets
        for (int i = 4; i < length; i++) {
            passwordBuilder.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the characters to randomize their positions
        List<Character> passwordChars = new ArrayList<>();
        for (char c : passwordBuilder.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars, random); // Use SecureRandom for shuffling

        StringBuilder finalPassword = new StringBuilder();
        for (char c : passwordChars) {
            finalPassword.append(c);
        }

        return finalPassword.toString();
    }

    private String generateFingerPrintHash(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
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
