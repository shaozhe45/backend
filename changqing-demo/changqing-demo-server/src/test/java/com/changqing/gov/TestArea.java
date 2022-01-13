package com.changqing.gov;

import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.demo.dao.CCommonAreaMapper;
import com.changqing.gov.demo.entity.CCommonArea;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is a Description
 *
 * @author changqing
 * @date 2019/08/20
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class TestArea {
    @Autowired
    private CCommonAreaMapper mapper;

    @Test
    public void test() {

        Long id = 585823974982680865L;
        CCommonArea cCommonArea = mapper.selectById(id);
        System.out.println(cCommonArea);

        BaseContextHandler.setName("changqing_authority_dev");
        cCommonArea = mapper.selectById(id);
        System.out.println(cCommonArea);

    }

}
