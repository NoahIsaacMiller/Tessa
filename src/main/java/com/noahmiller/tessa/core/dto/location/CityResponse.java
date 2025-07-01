package com.noahmiller.tessa.core.dto.location;

import lombok.Data;

@Data
public class CityResponse {
    private Long id;
    private String nameKey;
    private String localizedName;
    private String code;
    private Long provinceId;
    private String provinceLocalizedName; // 关联省份的国际化名称
    private Long countryId; // 方便前端显示完整的层级
    private String countryLocalizedName;
}