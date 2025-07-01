package com.noahmiller.tessa.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "countries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nameKey; // 修改为 nameKey，存储国际化 Key

    @Column(nullable = false, unique = true, length = 10)
    private String code; // 国家代码，如 "CN", "US"
}