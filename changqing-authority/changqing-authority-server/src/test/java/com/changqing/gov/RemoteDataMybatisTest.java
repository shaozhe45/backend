package com.changqing.gov;

import com.changqing.gov.oauth.api.OrgApi;
import com.changqing.gov.authority.dao.auth.MenuMapper;
import com.changqing.gov.authority.dao.auth.ResourceMapper;
import com.changqing.gov.authority.dao.auth.RoleMapper;
import com.changqing.gov.authority.dao.auth.UserMapper;
import com.changqing.gov.authority.dao.common.AreaMapper;
import com.changqing.gov.authority.dao.core.StationMapper;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.entity.core.Station;
import com.changqing.gov.authority.service.auth.ResourceService;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.authority.service.core.OrgService;
import com.changqing.gov.authority.service.core.impl.StationServiceImpl;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.query.LbqWrapper;
import com.changqing.gov.injection.core.InjectionCore;
import com.changqing.gov.model.RemoteData;
import com.changqing.gov.oauth.api.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = AuthorityApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@WebAppConfiguration
public class RemoteDataMybatisTest {
    @Autowired
    ResourceService resourceService;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired

    private OrgService orgService;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private StationServiceImpl stationServiceImpl;
    @Autowired
    private InjectionCore injectionCore;
    @Autowired
    private OrgApi orgApi;
    @Autowired
    private UserApi userApi;
    @Before
    public void setTenant() {
        BaseContextHandler.setTenant("0000");
    }

    @Test
    public void testGet() {
        User user = new User();
//        user.setSex(Sex.W);
        LbqWrapper<User> wrapper = Wraps.lbQ(user);
        wrapper
//                .geHeader(User::getCreateTime, LocalDateTime.MIN)
//                .leFooter(User::getCreateTime, LocalDateTime.MAX)
                .like(User::getAccount, "changqing")
                .nested(i -> i.like(User::getName, "")
                        .or().like(User::getMobile, "")
                ).orderByDesc(User::getCreateTime);

        List<User> list = userService.list(wrapper);

        System.out.println(list);

    }

    @Test
    public void testFeign() {
        Set<Serializable> ids = new HashSet<>();
        ids.add("132758199328920593");
        Map<Serializable, Object> orgByIds = orgApi.findOrgByIds(ids);
        System.out.println(orgByIds.size());
    }

    @Test
    public void testFeignUser() {
        Set<Serializable> ids = new HashSet<>();
        ids.add("151994979890702370");
        ids.add("144249457343923281");
        Map<Serializable, Object> orgByIds = userApi.findUserNameByIds(ids); //.findOrgByIds(ids);
        System.out.println(orgByIds.size());
    }

    @Test
    public void testSave3() {
        Station station = Station.builder()
                .name("test4")
                .orgId(new RemoteData<>(4L))
                .build();
        stationMapper.insert(station);
    }


    @Test
    public void test345() {
        RemoteData<Long, Station> stationData = new RemoteData<>(101L);

        Station station = stationMapper.selectById(stationData);
        System.out.println(station);



//        injectionCore.injection(station2);
//        System.out.println(station2);
    }


    @Test
    public void test343() {
        TestModel obj = new TestModel();

        obj.setEducation(new RemoteData<>("COLLEGE"));
        obj.setEducation2("BOSHI");

        obj.setStation(new RemoteData<>(101L));
        obj.setError(new RemoteData<>(101L));

        injectionCore.injection(obj);
        System.out.println(obj);
    }


}
