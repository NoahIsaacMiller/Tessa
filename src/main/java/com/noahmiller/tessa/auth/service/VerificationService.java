package com.noahmiller.tessa.auth.service;

import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;

public interface VerificationService {

    void sendVerificationCode(VerificationChannel channel, String target, VerificationType type);
    boolean verifyVerificationCode(VerificationChannel channel, String target,
                                   String code, VerificationType type);
}