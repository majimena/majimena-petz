package org.majimena.petical.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Data関連の設定.
 */
@Configuration
@EnableJpaRepositories({"org.majimena.petical.repository", "org.majimena.petical.config.audit"})
@EnableJpaAuditing
@EnableTransactionManagement
public class SpringDataConfiguration {
}
