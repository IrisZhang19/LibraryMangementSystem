package com.librarymanagement.project.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * AuthEntryPointJwt implements AuthenticationEntryPoint that handles unauthorized
 * access attempts and validation errors. It customizes the response for authentication exceptions
 * by sending appropriate HTTP status codes and error messages.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Check if the error is a validation exception (MethodArgumentNotValidException)
        Object exception = request.getAttribute("jakarta.servlet.error.exception");

        // If validation fails, send a 400 Bad Request error
        if (exception instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
            logger.error("Bad Request error : {}", authException.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // Collect all validation error messages
            Map<String, String> errors = new HashMap<>();
            ((org.springframework.web.bind.MethodArgumentNotValidException) exception)
                    .getBindingResult()
                    .getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

            // Send the response with validation error details
            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errors);
        } else {
            // If error is indeed authentication error, send a 401 Unauthorized error
            logger.error("Unauthorized error: {}", authException.getMessage());


            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", authException.getMessage());
            body.put("path", request.getServletPath());

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        }
    }
}

