package com.changqing.gov.authority.api;

import com.changqing.gov.authority.api.hystrix.UserBizApiFallback;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户
 *
 * @author changqing
 * @date 2019/07/02
 */
@FeignClient(name = "${changqing.feign.authority-server:changqing-authority-server}", fallback = UserBizApiFallback.class
        , path = "/user", qualifier = "userBizApi")
public interface UserBizApi {

    /**
     * 查询所有的用户id
     *
     * @return
     */
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    R<List<Long>> findAllUserId();

    /**
     * 根据id集合查询所有的用户
     *
     * @return
     */
    @RequestMapping(value = "/findUserById", method = RequestMethod.GET)
    R<List<User>> findUserById(@RequestParam(value = "ids") List<Long> ids);
}
