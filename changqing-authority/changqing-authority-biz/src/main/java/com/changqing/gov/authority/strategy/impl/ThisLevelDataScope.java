package com.changqing.gov.authority.strategy.impl;

import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.authority.strategy.AbstractDataScopeHandler;
import com.changqing.gov.model.RemoteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 本级
 *
 * @author changqing
 * @version 1.0
 * @date 2019-06-08 15:44
 */
@Component("THIS_LEVEL")
public class ThisLevelDataScope implements AbstractDataScopeHandler {
    @Autowired
    private UserService userService;

    @Override
    public List<Long> getOrgIds(List<Long> orgList, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        Long orgId = RemoteData.getKey(user.getOrg());
        return Arrays.asList(orgId);
    }
}
