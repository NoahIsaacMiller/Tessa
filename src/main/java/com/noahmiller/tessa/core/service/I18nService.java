package com.noahmiller.tessa.core.service;

import java.util.Locale;

public interface I18nService {

    /**
     * 根据 Key 获取当前语言环境下的国际化消息
     * @param key 消息 Key
     * @return 国际化后的消息
     */
    String getMessage(String key);

    /**
     * 根据 Key 和指定 Locale 获取国际化消息
     * @param key 消息 Key
     * @param locale 指定的语言环境
     * @return 国际化后的消息
     */
    String getMessage(String key, Locale locale);

    /**
     * 根据 Key 获取当前语言环境下的国际化消息，并带参数
     * @param key 消息 Key
     * @param args 参数
     * @return 国际化后的消息
     */
    String getMessage(String key, Object[] args);


    /**
     * 根据 Key 和指定 Locale 获取国际化消息，并带参数
     * @param key 消息 Key
     * @param args 参数
     * @param locale 指定的语言环境
     * @return 国际化后的消息
     */
    String getMessage(String key, Object[] args, Locale locale);
}