package org.example.userservice.security;

import org.example.userservice.models.Role;
import org.example.userservice.models.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CustomUserDetails implements UserDetails {

    private List<GrantedAuthority> grantedAuthorities;
    private String username;
    private String password;

    public CustomUserDetails(User user) {
        this.username = user.getName();
        this.password = user.getPassword();
        this.grantedAuthorities = new ArrayList<>();

        for(Role role : user.getRoles()) {
            grantedAuthorities.add(new CustomGrantedAuthority(role));
        }
    }

    public CustomUserDetails() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}