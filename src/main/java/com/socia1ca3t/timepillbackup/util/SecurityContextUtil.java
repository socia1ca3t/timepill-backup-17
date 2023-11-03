package com.socia1ca3t.timepillbackup.util;

import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import com.socia1ca3t.timepillbackup.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

public class SecurityContextUtil {


    public static UserInfo getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new RuntimeException("用户登录状态失效");
    }

    public static RestTemplate getCurrentUserBasicAuthRestTemplate() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            return userDetails.getUserBindBasicAuthRestTemplate();
        }

        throw new RuntimeException("用户登录状态失效");
    }

}
