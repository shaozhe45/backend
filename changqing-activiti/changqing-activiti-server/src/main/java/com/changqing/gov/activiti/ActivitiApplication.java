package com.changqing.gov.activiti;

import com.changqing.gov.security.annotation.EnableLoginArgResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@EnableAutoConfiguration(exclude = {
        org.activiti.spring.boot.SecurityAutoConfiguration.class
})
@EnableDiscoveryClient
@Configuration
@ComponentScan("com.changqing.gov")
@EnableFeignClients(value = {
        "com.changqing.gov",
})
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableLoginArgResolver
public class ActivitiApplication {

    public static void main(String[] args) throws UnknownHostException {

        ConfigurableApplicationContext application = SpringApplication.run(ActivitiApplication.class, args);
        Environment env = application.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "应用 '{}' 运行成功! 访问连接:\n\t" +
                        "Swagger文档: \t\thttp://{}:{}/doc.html\n\t" +
                        "数据库监控: \t\thttp://{}:{}/druid\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                "127.0.0.1",
                env.getProperty("server.port"));
    }

}
