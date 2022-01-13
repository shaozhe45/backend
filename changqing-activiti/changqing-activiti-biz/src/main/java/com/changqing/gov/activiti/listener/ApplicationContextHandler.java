package com.changqing.gov.activiti.listener;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 注入配置获取
 *
 * @author wz
 * @date 2020-08-07
 */
@Component
public class ApplicationContextHandler implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHandler.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ApplicationContextHandler.applicationContext;
    }

    public static Object getBean(String name) {
        return ApplicationContextHandler.applicationContext.getBean(name);
    }
}
