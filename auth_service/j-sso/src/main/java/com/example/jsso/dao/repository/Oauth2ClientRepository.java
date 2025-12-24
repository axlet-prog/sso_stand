package com.example.jsso.dao.repository;

import com.example.jsso.dao.entity.SystemOauth2ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2ClientRepository extends JpaRepository<SystemOauth2ClientEntity, Long> {

    SystemOauth2ClientEntity getByClientId(String clientId);

    // @Override
    // public void afterPropertiesSet() throws Exception {
    //     this.save(UserEntity.builder()
    //                   .email("admin@example.com")
    //                   .passwordHash("{noop}admin@example.com")
    //                   .active(true)
    //                   .firstName("Admin")
    //                   .secondName("Admin")
    //                   .birthday(LocalDate.of(2004, 4, 21))
    //                   .build());
    // }
}
