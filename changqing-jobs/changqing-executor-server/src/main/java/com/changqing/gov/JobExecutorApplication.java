package com.changqing.gov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author changqing 2020年04月10日21:34:04
 */
@EnableFeignClients(value = {
		"com.changqing.gov",
})
@SpringBootApplication
public class JobExecutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobExecutorApplication.class, args);
	}

}
