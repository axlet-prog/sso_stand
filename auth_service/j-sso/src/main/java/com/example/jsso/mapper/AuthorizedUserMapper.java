package com.example.jsso.mapper;

import com.example.jsso.constants.AuthProvider;
import com.example.jsso.dao.entity.UserEntity;
import com.example.jsso.dto.AuthorizedUser;
import java.util.Collections;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthorizedUserMapper {

    public AuthorizedUser map(UserEntity entity, AuthProvider provider) {
        return AuthorizedUser.builder(entity.getEmail(), entity.getPasswordHash(), Collections.emptyList())
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .secondName(entity.getSecondName())
            .middleName(entity.getMiddleName())
            .birthday(entity.getBirthday())
            .avatarUrl(entity.getAvatarUrl())
            .build();
    }
}
