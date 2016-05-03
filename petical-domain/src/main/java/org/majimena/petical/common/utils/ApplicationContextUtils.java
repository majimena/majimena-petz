package org.majimena.petical.common.utils;

import org.majimena.petical.security.GrantedAuthorityService;
import org.majimena.petical.security.SecurityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * アプリケーションコンテキストのユーティリティ.
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware, InitializingBean {

    /**
     * アプリケーションコンテキスト.
     */
    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        GrantedAuthorityService bean = applicationContext.getBean(GrantedAuthorityService.class);
        SecurityUtils.setGrantedAuthorityService(bean);
    }
}
