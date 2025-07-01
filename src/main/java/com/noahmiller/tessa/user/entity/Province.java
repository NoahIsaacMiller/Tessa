package com.noahmiller.tessa.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "provinces")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nameKey; // 修改为 nameKey，存储国际化 Key

    @Column(length = 20)
    private String code; // 省份代码，如 "SC" (四川)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_PROVINCE_COUNTRY_ID"))
    private Country country; // 关联到 Country 实体
}