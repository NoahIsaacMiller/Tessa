package com.noahmiller.tessa.common.service.impl; // 放在 impl 子包下

import com.noahmiller.tessa.common.service.I18nService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service // 标记为 Spring Bean
public class I18nServiceImpl implements I18nService {

    private final MessageSource messageSource;

    public I18nServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, key, locale);
    }

    @Override
    public String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }

    @Override
    public String getMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, key, locale);
    }
}