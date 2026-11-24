package com.example.jsso.dao.entity;

import com.example.jsso.dao.entity.common.VersionedBusinessEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "sso", name = "users")
public class UserEntity extends VersionedBusinessEntity<UUID> {

	@Id
	@Column(name = "user_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(name = "email", nullable = false)
	private String email;
	@Column(name = "password_hash")
	private String passwordHash;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String secondName;
	@Column(name = "middle_name")
	private String middleName;
	@Column(name = "birthday")
	private LocalDate birthday;
	@Column(name = "avatar_url")
	private String avatarUrl;
	@Column(name = "active", nullable = false)
	private Boolean active;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(schema = "sso", name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private List<RoleEntity> roles;

	@Override
	public UUID getId() {
		return this.id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;
	}

}
