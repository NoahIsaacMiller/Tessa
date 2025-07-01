package com.noahmiller.tessa.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增策略
    private Long id;

    @Column(nullable = false, unique = true, length = 50) // 城市名称通常非空且唯一
    private String name;

    @Column(name = "province_name", length = 50) // 如果列名不一致，使用 name 属性
    private String provinceName; // 假设 City 实体中包含省份名称字段
}