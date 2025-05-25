package com.noahmiller.tessa.common.mail.service;

public interface EmailContentBuilder {
    String buildEmailContent();
    String getSubject();
}
