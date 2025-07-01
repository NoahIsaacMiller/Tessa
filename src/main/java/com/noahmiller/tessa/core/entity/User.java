package com.noahmiller.tessa.core.entity; // 建议改为 com.noahmiller.tessa.model.entity

import com.noahmiller.tessa.user.entity.City;
import com.noahmiller.tessa.user.entity_old.Gender;
import com.noahmiller.tessa.user.entity_old.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 64)
    private String salt;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 50)
    private String province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = true,
            foreignKey = @ForeignKey(name = "FK_USER_CITY_ID"))
    private City city;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}