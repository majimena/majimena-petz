package org.majimena.petical.config;

import org.majimena.petical.config.aspect.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
@EnableAspectJAutoProxy
public class DebugLoggingAspectConfiguration {
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
