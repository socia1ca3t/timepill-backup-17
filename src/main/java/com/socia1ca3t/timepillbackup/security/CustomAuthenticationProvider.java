package com.socia1ca3t.timepillbackup.security;

import com.socia1ca3t.timepillbackup.pojo.dto.UserInfo;
import com.socia1ca3t.timepillbackup.properties.TimepillConfig;
import com.socia1ca3t.timepillbackup.util.JacksonUtil;
import com.socia1ca3t.timepillbackup.util.RestTemplateUtil;
import com.socia1ca3t.timepillbackup.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProvider.class);

    private static final String USER_API = SpringContextUtil.getBean(TimepillConfig.class).getApiUserURL();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        final RestTemplate basicAuthTemplate = getUserBindBasicAuthRestTemplate(username, password);

        UserInfo user;

        try {
            ResponseEntity<String> entity = basicAuthTemplate.getForEntity(USER_API, String.class);
            user = JacksonUtil.jsonToBean(entity.getBody(), UserInfo.class);

        } catch (HttpClientErrorException e) {

            if (401 == e.getStatusCode().value()) {
                throw new UsernameNotFoundException("用户名或密码错误");
            }
            throw new UsernameNotFoundException(e.getResponseBodyAsString());

        } catch (Exception e) {
            logger.error("鉴权异常", e);
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

        if (user == null) {
            throw new AuthenticationServiceException("未获取到用户登录信息");
        }

        user.setEmail(username);
        CustomUserDetails userDetails = new CustomUserDetails(user, basicAuthTemplate);

        // 返回认证令牌
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }


    private RestTemplate getUserBindBasicAuthRestTemplate(String email, String password) {


        HttpComponentsClientHttpRequestFactory fa;
        try {
            fa = RestTemplateUtil.getHTTPClientFactory();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new RestTemplateBuilder()
                .requestFactory(() -> fa)
                .basicAuthentication(email, password)
                .build();
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

