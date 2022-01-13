package com.changqing.gov.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.group.dao.KnowledgeRelationMapper;
import com.changqing.gov.group.dto.KnowledgeRelationPageDTO;
import com.changqing.gov.group.entity.KnowledgeLibrary;
import com.changqing.gov.group.entity.KnowledgeRelation;
import com.changqing.gov.group.service.KnowledgeRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 作业关联配置表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class KnowledgeRelationServiceImpl extends SuperServiceImpl<KnowledgeRelationMapper, KnowledgeRelation> implements KnowledgeRelationService {
    @Override
    public IPage<KnowledgeRelation> getRelationPage(PageParams<KnowledgeRelationPageDTO> params) {
        IPage<KnowledgeRelation> page = params.buildPage();
        KnowledgeRelation model = BeanUtil.toBean(params.getModel(), KnowledgeRelation.class);
        IPage<KnowledgeRelation> relationPage = baseMapper.getRelationPage(page, model);
        return relationPage;
    }
}
