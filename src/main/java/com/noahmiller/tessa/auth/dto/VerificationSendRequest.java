package com.noahmiller.tessa.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class VerificationSendRequest {
    @Email(message = "邮箱格式不正确")
    private String email;
}
