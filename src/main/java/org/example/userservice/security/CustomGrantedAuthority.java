package org.example.userservice.security;

import org.example.userservice.models.Role;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomGrantedAuthority implements GrantedAuthority {
    //role in our service is mapped with Authority in spring service
    private Role role;

    public CustomGrantedAuthority(Role role) {
        this.role = role;
    }
    public CustomGrantedAuthority() {}

    @Override
    public @Nullable String getAuthority() {
        return role.getValue();
    }
}