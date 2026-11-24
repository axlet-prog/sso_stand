package com.example.jsso.mapper;

import com.example.jsso.constants.AuthProvider;
import com.example.jsso.dao.entity.AuthorityEntity;
import com.example.jsso.dao.entity.RoleEntity;
import com.example.jsso.dao.entity.UserEntity;
import com.example.jsso.dto.AuthorizedUser;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AuthorizedUserMapper {

	public AuthorizedUser map(UserEntity entity, AuthProvider provider) {
		List<GrantedAuthority> authorities = getUserAuthorities(entity);
		return AuthorizedUser.builder(entity.getEmail(), entity.getPasswordHash(), authorities)
				.id(entity.getId())
				.firstName(entity.getFirstName())
				.secondName(entity.getSecondName())
				.middleName(entity.getMiddleName())
				.birthday(entity.getBirthday())
				.avatarUrl(entity.getAvatarUrl())
				.build();
	}

	public List<GrantedAuthority> getUserAuthorities(UserEntity user) {
		return user.getRoles().stream()
				.filter(RoleEntity::getActive)
				.flatMap(role -> role.getAuthorities().stream())
				.filter(AuthorityEntity::getActive)
				.map(authority -> new SimpleGrantedAuthority(authority.getCode()))
				.distinct()
				.collect(Collectors.toList());
	}
}
