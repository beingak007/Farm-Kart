package com.farmkart.common.rest.security;

import com.farmkart.starter.common.enums.UserRoleEnum;
import com.farmkart.starter.common.security.FkSecurityPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class FkUserPrincipal implements UserDetails, FkSecurityPrincipal {

    private final Long userId;
    private final UserRoleEnum role;

    public FkUserPrincipal(Long userId, UserRoleEnum role) {
        this.userId = userId;
        this.role = role != null ? role : UserRoleEnum.CUSTOMER;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Long userId() {
        return userId;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    @Override
    public UserRoleEnum role() {
        return role;
    }

    public boolean isMasterAdmin() {
        return role.isMasterAdmin();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.authority()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
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
}
