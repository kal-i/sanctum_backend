package com.kali.sanctum.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final RequestMappingHandlerMapping handlerMapping;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        boolean endpointExists = handlerMapping.getHandlerMethods()
                .keySet().stream().anyMatch(info -> {
                    var condition = info.getPatternsCondition();
                    if (condition == null)
                        return false;
                    return condition.getPatterns().stream()
                            .anyMatch(pattern -> matchesPattern(pattern, request.getServletPath()));
                });

        final Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("timestamp", System.currentTimeMillis());

        if (endpointExists) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", "You are not authenticated. Please log in to access this resource.");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            body.put("error", "Not Found");
            body.put("message", "The request endpoint does not exist.");
        }

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    private boolean matchesPattern(String pattern, String path) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, path);
    }
}
