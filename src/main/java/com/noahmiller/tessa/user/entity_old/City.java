package com.noahmiller.tessa.user.entity_old;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {
    private Long id;
    private String name;
    private String provinceName;
}
