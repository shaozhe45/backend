package com.changqing.gov.tenant.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.authority.dto.auth.UserUpdatePasswordDTO;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.tenant.dao.GlobalUserMapper;
import com.changqing.gov.tenant.dto.GlobalUserSaveDTO;
import com.changqing.gov.tenant.dto.GlobalUserUpdateDTO;
import com.changqing.gov.tenant.entity.GlobalUser;
import com.changqing.gov.tenant.service.GlobalUserService;
import com.changqing.gov.utils.BeanPlusUtil;
import com.changqing.gov.utils.BizAssert;
import com.changqing.gov.utils.StrHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.changqing.gov.utils.BizAssert.isFalse;

/**
 * <p>
 * 业务实现类
 * 全局账号
 * </p>
 *
 * @author changqing
 * @date 2019-10-25
 */
@Slf4j
@Service
@DS("master")
public class GlobalUserServiceImpl extends SuperServiceImpl<GlobalUserMapper, GlobalUser> implements GlobalUserService {

    @Override
    public Boolean check(String account) {
        return super.count(Wraps.<GlobalUser>lbQ()
                .eq(GlobalUser::getAccount, account)) > 0;
    }

    /**
     * @param data
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GlobalUser save(GlobalUserSaveDTO data) {
        BizAssert.equals(data.getPassword(), data.getConfirmPassword(), "2次输入的密码不一致");
        isFalse(check(data.getAccount()), "账号已经存在");

        String md5Password = SecureUtil.md5(data.getPassword());

        GlobalUser globalAccount = BeanPlusUtil.toBean(data, GlobalUser.class);
        // 全局表就不存用户数据了
        globalAccount.setPassword(md5Password);
        globalAccount.setName(StrHelper.getOrDef(data.getName(), data.getAccount()));
        globalAccount.setReadonly(false);

        save(globalAccount);
        return globalAccount;
    }

    /**
     * @param data
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GlobalUser update(GlobalUserUpdateDTO data) {
        GlobalUser globalUser = BeanPlusUtil.toBean(data, GlobalUser.class);
        globalUser.setPassword(null);
        updateById(globalUser);
        return globalUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePassword(UserUpdatePasswordDTO data) {
        BizAssert.equals(data.getConfirmPassword(), data.getPassword(), "密码与确认密码不一致");

        GlobalUser user = getById(data.getId());
        BizAssert.notNull(user, "用户不存在");

        GlobalUser build = GlobalUser.builder().password(SecureUtil.md5(data.getPassword())).id(data.getId()).build();
        return updateById(build);
    }
}
