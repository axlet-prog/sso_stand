package com.example.jresource.dto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Collection<String> authorities;
}