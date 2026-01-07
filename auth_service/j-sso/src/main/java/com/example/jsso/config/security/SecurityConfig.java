package com.example.jsso.config.security;

import com.example.jsso.service.CustomOAuth2UserService;
import com.example.jsso.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomUserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailService)
            .passwordEncoder(passwordEncoder);

        http
            .with(
                new SocialConfigurer(), configurer ->
                    configurer.oAuth2UserService(oAuth2UserService)
            )
            .authorizeHttpRequests(authorize ->
                                       authorize
                                           .requestMatchers(
                                               "/error",
                                               "/favicon.ico",
                                               "/.well-known/**"
                                           ).permitAll()
                                           .requestMatchers(
                                               "/v3/api-docs",
                                               "/swagger-ui/**",
                                               "/v3/api-docs/swagger-config"
                                           ).permitAll()
                                           .anyRequest().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());

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


}
