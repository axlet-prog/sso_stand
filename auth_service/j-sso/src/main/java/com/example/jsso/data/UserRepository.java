package com.example.jsso.data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements InitializingBean {

    private final Map<UUID, UserEntity> store = new HashMap<>();

    public UserEntity save(UserEntity user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }

        this.store.put(user.getId(), user);
        return user;
    }

    public boolean delete(UUID id) {
        UserEntity user = store.getOrDefault(id, null);
        if (user != null) {
            this.store.remove(id);
        }
        return true;
    }

    public UserEntity findById(UUID id) {
        return this.store.getOrDefault(id, null);
    }

    public UserEntity findByEmail(String email) {
        return this.store.values().stream()
            .filter(item -> item.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.save(UserEntity.builder()
                      .email("admin@example.com")
                      .passwordHash("{noop}admin@example.com")
                      .active(true)
                      .firstName("Admin")
                      .secondName("Admin")
                      .birthday(LocalDate.of(2004, 4, 21))
                      .build());
    }
}
