package com.wjdansrud.springauthserverbase.auth.config;

import com.wjdansrud.springauthserverbase.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {
    private final User user;
    private final int id;
    private final String roleKey;

    public UserPrincipal(User u) {
        this.user = u;
        this.id = u.getId();
        this.roleKey = u.getRole().getKey();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(roleKey));
    }

    @Override
    public String getPassword() {
        return null; // JWT 인증 방식이라면 패스워드 필요 없음
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Username을 반환
    }

    public User getUser() {
        return this.user;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}
