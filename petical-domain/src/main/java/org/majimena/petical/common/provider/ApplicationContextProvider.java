package org.majimena.petical.common.provider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * アプリケーションコンテキストプロバイダ.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    /**
     * アプリケーションコンテキスト.
     */
    private static ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
