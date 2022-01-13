package com.changqing.gov.authority.controller.auth;


import cn.hutool.core.util.RandomUtil;
import com.changqing.gov.authority.dto.auth.ApplicationPageDTO;
import com.changqing.gov.authority.dto.auth.ApplicationSaveDTO;
import com.changqing.gov.authority.dto.auth.ApplicationUpdateDTO;
import com.changqing.gov.authority.entity.auth.Application;
import com.changqing.gov.authority.service.auth.ApplicationService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperCacheController;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 应用
 * </p>
 *
 * @author changqing
 * @date 2019-12-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/application")
@Api(value = "Application", tags = "应用")
@PreAuth(replace = "application:")
public class ApplicationController extends SuperCacheController<ApplicationService, Long, Application, ApplicationPageDTO, ApplicationSaveDTO, ApplicationUpdateDTO> {

    @Override
    public R<Application> handlerSave(ApplicationSaveDTO applicationSaveDTO) {
        applicationSaveDTO.setClientId(RandomUtil.randomString(24));
        applicationSaveDTO.setClientSecret(RandomUtil.randomString(32));
        return super.handlerSave(applicationSaveDTO);
    }

}
