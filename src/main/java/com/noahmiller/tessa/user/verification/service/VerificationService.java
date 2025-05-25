package com.noahmiller.tessa.user.verification.service;

import com.noahmiller.tessa.user.verification.enums.VerificationType;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Value;

public interface VerificationService {
    void sendVerificationCode(String email, VerificationType register);
    boolean verifyVerificationCode(String email, String code, VerificationType register);
    // 防刷
    boolean canSendVerificationCode(String email, VerificationType register);

}
