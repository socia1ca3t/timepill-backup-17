package com.socia1ca3t.timepillbackup.config;

import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import com.socia1ca3t.timepillbackup.security.CustomUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserAccessResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {


        if (parameter.getParameterType().isAssignableFrom(UserInfo.class)
                && parameter.hasParameterAnnotation(CurrentUser.class)) {
            return true;
        } else if (parameter.getParameterType().isAssignableFrom(RestTemplate.class)
                && parameter.hasParameterAnnotation(CurrentUserBasicAuthRestTemplate.class)) {
            return true;
        }

        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            if (parameter.getParameterType().isAssignableFrom(UserInfo.class)) {
                return userDetails.getUser();

            } else if (parameter.getParameterType().isAssignableFrom(RestTemplate.class)) {
                return userDetails.getUserBindBasicAuthRestTemplate();
            }

        }
        return null;
    }

}