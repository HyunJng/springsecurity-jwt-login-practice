package com.teamo.teamo.domain;

import com.teamo.teamo.type.Role;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "USER_TABLE")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;
    private String provider_id;

    public String getRoleKey() {
        return role.getKey();
    }

    public User update(String name) {
        this.name = name;
        return this;
    }
}
