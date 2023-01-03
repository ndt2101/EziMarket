package com.ndt2101.ezimarket.security;

import com.ndt2101.ezimarket.model.UserAccountEntity;
import com.ndt2101.ezimarket.model.UserLoginDataEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private Boolean isActive;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(UserAccountEntity userAccount) {
        if (userAccount instanceof UserLoginDataEntity) {
            return UserPrincipal.builder()
                    .id(userAccount.getId())
                    .username(((UserLoginDataEntity) userAccount).getLoginName())
                    .password(((UserLoginDataEntity) userAccount).getPassword())
                    .isActive(((UserLoginDataEntity) userAccount).getEmailValidationStatus().getStatusDescription().equals("valid"))
                    .authorities(userAccount.getRole().getPermission().stream()
                            .map(permissionEntity -> new SimpleGrantedAuthority(permissionEntity.getDescription()))
                            .collect(Collectors.toList()))
                    .build();
        } else {
            return null;
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
        return isActive;
    }
}
