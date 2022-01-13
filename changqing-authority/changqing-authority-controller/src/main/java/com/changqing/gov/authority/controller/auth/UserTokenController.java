package com.changqing.gov.authority.controller.auth;

import com.changqing.gov.authority.dto.auth.UserTokenPageDTO;
import com.changqing.gov.authority.dto.auth.UserTokenSaveDTO;
import com.changqing.gov.authority.dto.auth.UserTokenUpdateDTO;
import com.changqing.gov.authority.entity.auth.UserToken;
import com.changqing.gov.authority.service.auth.UserTokenService;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * token
 * </p>
 *
 * @author changqing
 * @date 2020-04-02
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/userToken")
@Api(value = "UserToken", tags = "token")
@PreAuth(replace = "userToken:")
public class UserTokenController extends SuperController<UserTokenService, Long, UserToken, UserTokenPageDTO, UserTokenSaveDTO, UserTokenUpdateDTO> {


}
