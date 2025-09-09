package com.kali.sanctum.security.user;

import com.kali.sanctum.enums.Role;
import com.kali.sanctum.model.Permission;
import com.kali.sanctum.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getProfileImageUrl() { return user.getProfileImageUrl(); }

    public Set<Permission> getPermissions() {
        return user.getPermissions();
    }

    public Role getRole() {
        return user.getRole();
    }

    public boolean isVerified() {
        return user.isVerified();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert user role and permissions to GrantedAuthority
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Add permissions
        user.getPermissions().forEach(
                permission -> authorities.add(new SimpleGrantedAuthority(permission.getName()))
        );

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
