package com.socia1ca3t.timepillbackup.security;

import com.socia1ca3t.timepillbackup.pojo.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final UserDTO user;
    private final RestTemplate userBindBasicAuthRestTemplate;
    private static final List<GrantedAuthority> ROLE_USER = Collections.unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER"));

    CustomUserDetails(UserDTO user, RestTemplate userBindBasicAuthRestTemplate) {

        this.user = user;
        this.userBindBasicAuthRestTemplate = userBindBasicAuthRestTemplate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ROLE_USER;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserDTO getUser() {

        UserDTO copiedUserInfo = new UserDTO();
        BeanUtils.copyProperties(user, copiedUserInfo);
        return copiedUserInfo;
    }

    public RestTemplate getUserBindBasicAuthRestTemplate() {
        return userBindBasicAuthRestTemplate;
    }
}
