package com.mealtracker.meal_tracker_api.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Validates the JWT on every request and, if valid, stores the
 * authenticated userId as a request attribute for controllers to read.
 * Public endpoints (signup/login/ping) are excluded via shouldNotFilter.
 *
 * This replaces the earlier placeholder pattern of trusting a raw
 * X-User-Id header - that header could be set to anyone's ID by any
 * client with no proof of identity at all. A validated JWT is the actual
 * proof.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String USER_ID_ATTRIBUTE = "authenticatedUserId";

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/ping")
                || path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or malformed Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        if (!jwtUtil.isValid(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        UUID userId = jwtUtil.extractUserId(token);
        request.setAttribute(USER_ID_ATTRIBUTE, userId);

        filterChain.doFilter(request, response);
    }
}
