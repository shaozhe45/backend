package com.changqing.gov;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;

import java.util.Properties;

/**
 * 简单工具测试类
 *
 * @author changqing
 * @date 2019/08/06
 */
public class NoBootTest {
    @Test
    public void test() {
        Cache<String, Object> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .build();

        Object val = cache.get("hello", (key) -> "延迟加载" + key);
        System.out.println(val);

        System.out.println(cache.getIfPresent("hello"));

    }

    @Test
    public void test2() {
        String data = "hello:\n" +
                "  swagger:\n" +
                "    enabled: true\n" +
                "    title: 网关模块\n" +
                "    base-package: com.changqing.gov.zuul.controller\n" +
                "\n" +
                "zuul:\n" +
                "  retryable: false\n" +
                "  servlet-path: /\n" +
                "  ignored-services: \"*\"\n" +
                "  sensitive-headers:\n" +
                "  ratelimit:\n" +
                "    key-prefix: gate_rate\n" +
                "    enabled: true\n" +
                "    repository: REDIS\n" +
                "    behind-proxy: true\n" +
                "    default-policy:\n" +
                "      cycle-type: 1\n" +
                "      limit: 10\n" +
                "      refresh-interval: 60\n" +
                "      type:\n" +
                "        - APP\n" +
                "        - URL\n" +
                "  routes:\n" +
                "    authority:\n" +
                "      path: /authority/**\n" +
                "      serviceId: changqing-authority-server\n" +
                "    file:\n" +
                "      path: /file/**\n" +
                "      serviceId: changqing-file-server\n" +
                "    msgs:\n" +
                "      path: /msgs/**\n" +
                "      serviceId: changqing-msgs-server\n" +
                "    order:\n" +
                "      path: /order/**\n" +
                "      serviceId: changqing-order-server\n" +
                "    demo:\n" +
                "      path: /demo/**\n" +
                "      serviceId: changqing-demo-server\n" +
                "\n" +
                "authentication:\n" +
                "  user:\n" +
                "    header-name: token\n" +
                "    pub-key: client/pub.key";

        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ByteArrayResource(data.getBytes()));
        Properties object = yamlFactory.getObject();
        System.out.println(object);
    }
}
