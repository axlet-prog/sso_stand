package com.example.jsso.dao.repository;

import com.example.jsso.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);

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
