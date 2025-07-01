package com.noahmiller.tessa.common.dto.location;

import lombok.Data;

@Data
public class CountryResponse {
    private Long id;
    private String nameKey; // 仍然保留原始 Key，用于内部识别或调试
    private String localizedName; // 用于前端显示的国际化名称
    private String code;
}
