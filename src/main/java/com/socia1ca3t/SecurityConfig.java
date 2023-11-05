package com.socia1ca3t;

import com.socia1ca3t.timepillbackup.security.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin(form -> form
                        .loginPage("/login")
                        // 使用客户端重定向，如果鉴权失败，failureForwardUrl 的服务器端重定向会导致无限循环的鉴权失败
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/home", true)
                        .permitAll())

                .logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/login"))

                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                // 若开启csrf，会导致 DispatcherType.Forward 重定向的 POST 请求由于未携带 csrf_token 而发生异常
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }


    @Bean
    public WebSecurityCustomizer apiStaticResources() {


        return (web) -> web.ignoring()
                .requestMatchers("/error")
                .requestMatchers("/notices")
                .requestMatchers("/index")
                .requestMatchers("/favicon.ico")
                .requestMatchers("/js/**")
                .requestMatchers("/icons/**")
                .requestMatchers("/images/**")
                .requestMatchers("/css/**");

    }


    @Bean
    public AuthenticationManager authenticationManager() {

        return new ProviderManager(new CustomAuthenticationProvider());
    }


}
