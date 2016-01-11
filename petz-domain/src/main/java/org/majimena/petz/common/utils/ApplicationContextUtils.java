package org.majimena.petz.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * アプリケーションコンテキストのユーティリティ.
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    /**
     * アプリケーションコンテキスト.
     */
    private static ApplicationContext applicationContext;

    /**
     * アプリケーションコンテキストを取得する.
     *
     * @return アプリケーションコンテキスト
     */
    public static ApplicationContext getApplicationContext() {
        return ApplicationContextUtils.applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }
}
