package com.mealtracker.meal_tracker_api.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
public class AuthenticatedUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUserId.class)
                && parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object userId = request.getAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE);

        if (userId == null) {
            // Should never actually happen for a filtered endpoint - the
            // filter would have already rejected the request with 401.
            // This is a defensive fallback, not the primary auth check.
            throw new IllegalStateException(
                    "No authenticated user found on request - is this endpoint excluded from JwtAuthFilter?"
            );
        }

        return userId;
    }
}
