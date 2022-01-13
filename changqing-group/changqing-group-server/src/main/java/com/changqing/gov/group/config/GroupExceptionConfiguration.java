package com.changqing.gov.group.config;

import com.changqing.gov.boot.handler.DefaultGlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 环保标准化管理平台-全局异常处理
 *
 * @author tanbao
 * @date 2021-08-24
 */
@Configuration
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GroupExceptionConfiguration extends DefaultGlobalExceptionHandler {
}
