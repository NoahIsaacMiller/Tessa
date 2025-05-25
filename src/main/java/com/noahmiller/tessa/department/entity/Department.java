package com.noahmiller.tessa.department.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department {
    private String id;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
