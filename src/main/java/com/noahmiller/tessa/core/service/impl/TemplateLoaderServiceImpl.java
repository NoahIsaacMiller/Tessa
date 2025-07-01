package com.noahmiller.tessa.common.service.impl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Component
public class TemplateLoaderServiceImpl {
    /**
     * 从classpath加载模板文件
     */
    public String getTemplate(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        try (Scanner scanner = new Scanner(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        }
    }
}