package com.changqing.gov.demo.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.changqing.gov.demo.dao.CCommonAreaMapper;
import com.changqing.gov.demo.entity.CCommonArea;
import com.changqing.gov.demo.service.CCommonAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 地区表
 * </p>
 *
 * @author changqing
 * @date 2019-08-20
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class CCommonAreaServiceImpl extends ServiceImpl<CCommonAreaMapper, CCommonArea> implements CCommonAreaService {

}
