package com.noahmiller.tessa.common.dto.location;

import lombok.Data;

@Data
public class ProvinceResponse {
    private Long id;
    private String nameKey;
    private String localizedName;
    private String code;
    private Long countryId;
    private String countryLocalizedName; // 关联国家的国际化名称
}
