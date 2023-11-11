package com.socia1ca3t.timepillbackup.security;

import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;
import com.socia1ca3t.timepillbackup.util.HttpClientUtil;
import com.socia1ca3t.timepillbackup.util.JacksonUtil;
import com.socia1ca3t.timepillbackup.util.TimepillUtil;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.util.TimeValue;
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

import java.net.URI;


public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProvider.class);

    private static final String USER_API = TimepillUtil.getConfig().apiUserURL();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        final RestTemplate basicAuthTemplate = getUserBindBasicAuthRestTemplate(username, password);

        UserDTO user;
        try {
            ResponseEntity<String> entity = basicAuthTemplate.getForEntity(USER_API, String.class);
            user = JacksonUtil.jsonToBean(entity.getBody(), UserDTO.class);

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


    @SneakyThrows
    private RestTemplate getUserBindBasicAuthRestTemplate(String email, String password) {

        HttpRequestRetryStrategy retryStrategy = new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(3));
        HttpRequestInterceptor basicAuthAddInterceptor = HttpClientUtil.getBasicAuthForTargetHOSTInterceptor(new URI(USER_API), email, password);

        HttpComponentsClientHttpRequestFactory httpRequestFactory  = HttpClientUtil.getHTTPSClientFactory(retryStrategy, basicAuthAddInterceptor);

        return new RestTemplateBuilder()
                .requestFactory(() -> httpRequestFactory)
                .build();
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

