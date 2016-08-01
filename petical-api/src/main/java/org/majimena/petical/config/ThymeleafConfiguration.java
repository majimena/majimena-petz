package org.majimena.petical.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Thymeleafのコンフィグレーション.
 */
@Slf4j
@Configuration
public class ThymeleafConfiguration {
    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("mails/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }
}
