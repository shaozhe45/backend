package com.changqing.gov.tenant.service;

import com.changqing.gov.authority.dto.auth.UserUpdatePasswordDTO;
import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.tenant.dto.GlobalUserSaveDTO;
import com.changqing.gov.tenant.dto.GlobalUserUpdateDTO;
import com.changqing.gov.tenant.entity.GlobalUser;

/**
 * <p>
 * 业务接口
 * 全局账号
 * </p>
 *
 * @author changqing
 * @date 2019-10-25
 */
public interface GlobalUserService extends SuperService<GlobalUser> {

    /**
     * 检测账号是否可用
     *
     * @param account
     * @return
     */
    Boolean check(String account);

    /**
     * 新建用户
     *
     * @param data
     * @return
     */
    GlobalUser save(GlobalUserSaveDTO data);


    /**
     * 修改
     *
     * @param data
     * @return
     */
    GlobalUser update(GlobalUserUpdateDTO data);

    /**
     * 修改密码
     *
     * @param model
     * @return
     */
    Boolean updatePassword(UserUpdatePasswordDTO model);
}
