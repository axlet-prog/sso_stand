package com.example.jsso.dao.repository;

import com.example.jsso.dao.entity.SystemOauth2ClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DaoClientRegisteredRepository implements RegisteredClientRepository {

	private final Oauth2ClientRepository repository;

	@Transactional
	@Override
	public void save(RegisteredClient clientDto) {
		SystemOauth2ClientEntity clientEntity = new SystemOauth2ClientEntity();
		if (clientDto.getId() != null) {
			clientEntity = repository.getReferenceById(Long.parseLong(clientDto.getId()));
		}
		this.map(clientDto, clientEntity);
		repository.save(clientEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public RegisteredClient findById(String id) {
		SystemOauth2ClientEntity clientEntity = repository.getReferenceById(Long.parseLong(id));
		return this.map(clientEntity);
	}

	@Transactional(readOnly = true)
	@Override
	public RegisteredClient findByClientId(String clientId) {
		SystemOauth2ClientEntity clientEntity = repository.getByClientId(clientId);

		return map(clientEntity);
	}

	private RegisteredClient map(SystemOauth2ClientEntity entity) {
		return RegisteredClient.withId(entity.getId().toString())
				.clientId(entity.getClientId())
				.clientSecret(entity.getClientSecret())
				.clientIdIssuedAt(entity.getClientIdIssueAt().toInstant(ZoneOffset.UTC))
				.clientSecretExpiresAt(entity.getClientSecretExpiresAt().toInstant(ZoneOffset.UTC))
				.clientName(entity.getClientName())
				.clientAuthenticationMethods(
						clientAuthenticationMethods -> clientAuthenticationMethods.addAll(entity.clientAuthenticationMethods()))
				.authorizationGrantTypes(
						authorizationGrantTypes -> authorizationGrantTypes.addAll(entity.authorizationGrantTypes()))
				.redirectUris(redirectUris -> redirectUris.addAll(entity.redirectUris()))
				.scopes(scopes -> scopes.addAll(entity.scopes()))
				.tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE).build())
				.build();
	}

	private void map(RegisteredClient dto, SystemOauth2ClientEntity entity) {
		entity.setClientId(dto.getClientId())
				.setClientIdIssueAt(
						dto.getClientIdIssuedAt() != null ? LocalDateTime.ofInstant(dto.getClientIdIssuedAt(), ZoneOffset.UTC)
								: null)
				.setClientSecret(dto.getClientSecret())
				.setClientSecretExpiresAt(
						dto.getClientSecretExpiresAt() != null ? LocalDateTime.ofInstant(
								dto.getClientSecretExpiresAt(),
								ZoneOffset.UTC
						) : null)
				.setClientName(dto.getClientName())
				.setClientAuthenticationMethods(dto.getClientAuthenticationMethods().stream()
						.map(ClientAuthenticationMethod::getValue)
						.collect(Collectors.joining(","))
				)
				.setAuthorizationGrantTypes(dto.getAuthorizationGrantTypes().stream()
						.map(AuthorizationGrantType::getValue)
						.collect(Collectors.joining(","))
				)
				.setRedirectUris(String.join(",", dto.getRedirectUris()))
				.setScopes(String.join(",", dto.getScopes()));
	}
}
