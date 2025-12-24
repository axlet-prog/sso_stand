package com.example.jsso.dto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntrospectionPrincipal {

    private UUID id;
    private String firstName;
    private String secondName;
    private String middleName;
    private LocalDate birthday;
    private String avatarUrl;
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public static IntrospectionPrincipal build(AuthorizedUser authorizedUser) {
        return IntrospectionPrincipal.builder()
            .id(authorizedUser.getId())
            .firstName(authorizedUser.getFirstName())
            .secondName(authorizedUser.getSecondName())
            .middleName(authorizedUser.getMiddleName())
            .birthday(authorizedUser.getBirthday())
            .avatarUrl(authorizedUser.getAvatarUrl())
            .username(authorizedUser.getUsername())
            .email(authorizedUser.getEmail())
            .authorities(authorizedUser.getAuthorities())
            .build();
    }
}