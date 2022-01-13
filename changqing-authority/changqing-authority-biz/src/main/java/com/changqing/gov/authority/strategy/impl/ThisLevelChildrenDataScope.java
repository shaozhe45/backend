package com.changqing.gov.authority.strategy.impl;

import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.entity.core.Org;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.authority.service.core.OrgService;
import com.changqing.gov.authority.strategy.AbstractDataScopeHandler;
import com.changqing.gov.model.RemoteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 本级以及子级
 *
 * @author changqing
 * @version 1.0
 * @date 2019-06-08 16:30
 */
@Component("THIS_LEVEL_CHILDREN")
public class ThisLevelChildrenDataScope implements AbstractDataScopeHandler {
    @Autowired
    private UserService userService;
    @Autowired
    private OrgService orgService;

    @Override
    public List<Long> getOrgIds(List<Long> orgList, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        Long orgId = RemoteData.getKey(user.getOrg());
        List<Org> children = orgService.findChildren(Arrays.asList(orgId));
        return children.stream().mapToLong(Org::getId).boxed().collect(Collectors.toList());
    }
}
