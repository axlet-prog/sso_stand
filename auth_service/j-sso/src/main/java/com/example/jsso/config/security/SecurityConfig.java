package com.example.jsso.config.security;

import com.example.jsso.config.properties.AuthorizationServerProperties;
import com.example.jsso.service.CustomOAuth2UserService;
import com.example.jsso.service.CustomUserDetailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomUserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationServerProperties authorizationServerProperties;

    // handlers
    private AuthenticationSuccessHandler oAuth2successHandler;
    private AuthenticationSuccessHandler loginRequestSuccessHandler;
    private AuthenticationFailureHandler failureHandler;

    public static final String LOGIN_PAGE = "/login";
    public static final String[] PERMIT_ALL_PATTERNS = {
        "/v3/api-docs",
        "/v3/api-docs/swagger-config",
        "/ui-test",
        "/favicon.ico",
        "/.well-known/**",
        "/static/**",
        LOGIN_PAGE
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailService)
            .passwordEncoder(passwordEncoder);

        http
            .csrf(AbstractHttpConfigurer::disable)
            .with(
                new SocialConfigurer(), configurer ->
                    configurer
                        .oAuth2UserService(oAuth2UserService)
                        .successHandler(oAuth2successHandler)
                        .failureHandler(failureHandler)
                        .formLogin(LOGIN_PAGE)
            )
            .authorizeHttpRequests(authorize ->
                                       authorize
                                           .requestMatchers(
                                               PERMIT_ALL_PATTERNS
                                           ).permitAll()
                                           .anyRequest().authenticated()
            )
            .formLogin(configurer -> {
                configurer.loginPage(LOGIN_PAGE)
                    .loginProcessingUrl(LOGIN_PAGE)
                    .successHandler(loginRequestSuccessHandler)
                    .failureHandler(failureHandler);
            });

        return http.build();
    }

    // @Bean
    // public UserDetailsService userDetailsService() {
    //     UserDetails user = User.builder()
    //         .username("admin")
    //         .password("{noop}password")
    //         .roles("USER")
    //         .build();
    //     return new InMemoryUserDetailsManager(user);
    // }
    @PostConstruct
    private void initializeHandlers() {
        // создаем кастомный AuthenticationSuccessHandler для формы логина
        this.loginRequestSuccessHandler = new CustomAuthenticationSuccessHandler(
            authorizationServerProperties.getAuthenticationSuccessUrl(),
            authorizationServerProperties.getCustomHandlerHeaderName()
        );

        // указываем стандартный AuthenticationSuccessHandler для OAuth2 Client
        this.oAuth2successHandler = new SimpleUrlAuthenticationSuccessHandler(
            authorizationServerProperties.getAuthenticationSuccessUrl()
        );
        this.failureHandler = new SimpleUrlAuthenticationFailureHandler();
    }

}
