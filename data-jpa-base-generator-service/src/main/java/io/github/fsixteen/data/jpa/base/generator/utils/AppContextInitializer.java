package io.github.fsixteen.data.jpa.base.generator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 缓存 Application Context.
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static Logger log = LoggerFactory.getLogger(AppContextInitializer.class);

    private static ConfigurableApplicationContext APPLICATION_CONTEXT = null;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        AppContextInitializer.APPLICATION_CONTEXT = applicationContext;
        log.info("Initialize Application Context.");
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return AppContextInitializer.APPLICATION_CONTEXT;
    }

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        AppContextInitializer.APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 返回唯一匹配给定对象类型(如果有的话)的 Bean 实例.
     * 
     * @param <T>      Bean 类型
     * @param clazz    Bean Class, 可为接口或超类
     * @param required 是否必须
     * @return T
     * @throws BeansException 若 Bean 实例获取失败
     */
    public static <T> T getBean(Class<T> clazz, boolean required) throws BeansException {
        try {
            return APPLICATION_CONTEXT.getBean(clazz);
        } catch (BeansException e) {
            if (!required) {
                return null;
            }
            throw e;
        }
    }

}
