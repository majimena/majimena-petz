package org.majimena.petz;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * ウェブアプリのテスト用コンフィグレーション.
 */
@Configuration
@ComponentScan(
        basePackageClasses = Application.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(ControllerAdvice.class))
public class WebAppTestConfiguration extends WebMvcConfigurationSupport {
}
