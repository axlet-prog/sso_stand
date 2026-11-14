package com.example.jsso.service;

import com.example.jsso.constants.AuthErrorCode;
import com.example.jsso.constants.AuthProvider;
import com.example.jsso.dao.entity.UserEntity;
import com.example.jsso.dao.repository.RoleRepository;
import com.example.jsso.dao.repository.UserRepository;
import com.example.jsso.dto.AuthorizedUser;
import com.example.jsso.exception.AuthException;
import com.example.jsso.mapper.AuthorizedUserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Создание или обновление пользователя
     */
    @Transactional
    @Override
    public UserEntity save(OAuth2User userDto, AuthProvider provider) {
        userDto.getAttributes().forEach((k, v) -> System.out.println(k + ": " + v));
        return switch (provider) {
            case GITHUB -> this.saveUserFromGithub(userDto);
            case GOOGLE -> this.saveUserFromGoogle(userDto);
            case YANDEX -> this.saveUserFromYandex(userDto);
        };
    }

    /**
     * Создание или обновление пользователя с последующим маппингом в сущность AuthorizedUser
     */
    @Transactional
    @Override
    public AuthorizedUser saveAndMap(OAuth2User userDto, AuthProvider provider) {
        UserEntity entity = this.save(userDto, provider);
        return AuthorizedUserMapper.map(entity, provider);
    }

    private UserEntity saveUserFromYandex(OAuth2User userDto) {
        String email = userDto.getAttribute("default_email");
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = getEntityByEmail(email);

        String firstName = userDto.getAttribute("first_name");
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        String lastName = userDto.getAttribute("last_name");
        if (lastName != null) {
            user.setSecondName(lastName);
        }
        return userRepository.save(user);
    }


    /**
     * Метод описывающий создание/обновление UserEntity на основе OAuth2User полученного из провайдера Github
     */
    private UserEntity saveUserFromGithub(OAuth2User userDto) {
        String email = userDto.getAttribute("email");
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = getEntityByEmail(email);

        String nameString = userDto.getAttribute("name");
        if (nameString != null) {
            String[] splitted = nameString.split(" ");
            user.setFirstName(splitted[0]);
            if (splitted.length > 1) {
                user.setSecondName(splitted[1]);
            }
            if (splitted.length > 2) {
                user.setMiddleName(splitted[2]);
            }
        } else {
            user.setFirstName(userDto.getAttribute("login"));
            user.setSecondName(userDto.getAttribute("login"));
        }

        if (userDto.getAttribute("avatar_url") != null) {
            user.setAvatarUrl(userDto.getAttribute("avatar_url"));
        }
        return userRepository.save(user);
    }

    /**
     * Метод описывающий создание/обновление UserEntity на основе OAuth2User полученного из провайдера Google
     */
    private UserEntity saveUserFromGoogle(OAuth2User userDto) {
        String email = userDto.getAttribute("email");
        if (email == null) {
            throw new AuthException(AuthErrorCode.EMAIL_IS_EMPTY);
        }
        UserEntity user = getEntityByEmail(email);

        if (userDto.getAttribute("given_name") != null) {
            user.setFirstName(userDto.getAttribute("given_name"));
        }

        if (userDto.getAttribute("family_name") != null) {
            user.setSecondName(userDto.getAttribute("family_name"));
        }

        if (userDto.getAttribute("picture") != null) {
            user.setAvatarUrl(userDto.getAttribute("picture"));
        }

        return userRepository.save(user);
    }

    private UserEntity getEntityByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setActive(true);
            user.setRoles(List.of(roleRepository.getDefaultRole()));
        }
        return user;
    }
}