package com.example.jsso.service;

import com.example.jsso.constants.AuthProvider;
import com.example.jsso.data.AuthorizedUser;
import com.example.jsso.data.UserEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService {

    UserEntity save(OAuth2User userDto, AuthProvider provider);

    AuthorizedUser saveAndMap(OAuth2User userDto, AuthProvider provider);

}