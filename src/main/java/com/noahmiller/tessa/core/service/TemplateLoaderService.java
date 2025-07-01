package com.noahmiller.tessa.core.service;

/**
 * 模板加载服务接口，用于从应用程序资源中加载模板文件。
 */
public interface TemplateLoaderService {

    /**
     * 从 Classpath 中指定路径加载模板文件内容。
     *
     * @param templateName 模板文件的名称（例如 "email/welcome.html"）。
     * 该名称会相对于 "classpath:templates/" 路径进行查找。
     * @return 模板文件的完整内容字符串。
     * @throws com.noahmiller.tessa.exception.TemplateLoadingException 如果模板文件不存在、无法读取或加载失败。
     */
    String getTemplate(String templateName);
}