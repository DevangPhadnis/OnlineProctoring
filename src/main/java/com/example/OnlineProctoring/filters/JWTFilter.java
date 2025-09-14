package com.example.OnlineProctoring.filters;

import com.example.OnlineProctoring.models.Session;
import com.example.OnlineProctoring.repository.SessionRepository;
import com.example.OnlineProctoring.serviceImpl.UserDetailsServiceImpl;
import com.example.OnlineProctoring.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    @Autowired
    ApplicationContext context;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Inside JWT Filter");

        String authorizationHeaders = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if(authorizationHeaders != null && authorizationHeaders.startsWith("Bearer")) {
            token = authorizationHeaders.substring(7);
            username = jwtUtils.extractUserName(token);
        }
        if(username != null) {
            UserDetails userDetails = context.getBean(UserDetailsServiceImpl.class).loadUserByUsername(username);
            if(jwtUtils.validateToken(token)) {
                String role = jwtUtils.extractUserRole(token);
                String activeSessionId = jwtUtils.extractSessionId(token);
                List<Session> sessionList = sessionRepository.findAll();

                boolean isPresent = sessionList.stream().anyMatch(session -> session.getSessionId().equals(activeSessionId));
                if(!isPresent) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"Unauthorized - Token is invalid or missing\"}");
                    return;
                }

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails
                        , null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
        logger.info("Outside JWT Filter");
    }
}
