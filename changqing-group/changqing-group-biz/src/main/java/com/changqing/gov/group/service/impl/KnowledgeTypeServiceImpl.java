package com.changqing.gov.group.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.group.dao.KnowledgeTypeMapper;
import com.changqing.gov.group.entity.KnowledgeType;
import com.changqing.gov.group.service.KnowledgeTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 知识类型字典表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class KnowledgeTypeServiceImpl extends SuperServiceImpl<KnowledgeTypeMapper, KnowledgeType> implements KnowledgeTypeService {
}
