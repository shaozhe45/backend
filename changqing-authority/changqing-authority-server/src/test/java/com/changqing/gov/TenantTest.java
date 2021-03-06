package com.changqing.gov;

import com.changqing.gov.authority.dao.auth.RoleMapper;
import com.changqing.gov.authority.dao.auth.UserMapper;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * This is a Description
 *
 * @author changqing
 * @date 2019/10/27
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class TenantTest {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;

    @Before
    public void setTenant() {
        BaseContextHandler.setTenant("0000");
    }

    @Test
    public void test() {
        List<Long> userIdByCode = roleMapper.findUserIdByCode(new String[]{"SUPER_ADMIN"});
        System.out.println(userIdByCode.size());
    }

    @Test
    public void testFindUserByRoleId() {
        List<User> list = userMapper.findUserByRoleId(100L, "ad%min");
        log.info("list.size= " + list.size());
    }

    @Test
    public void testList() {
//        LbqWrapper<User> query = null;
        LbqWrapper<User> query = Wraps.lbQ();
        query.eq(User::getName, "超管");
        query.like(User::getAccount, "changqing");
        query.orderByAsc(User::getCreateTime);
        userMapper.selectList(query);
    }


}
