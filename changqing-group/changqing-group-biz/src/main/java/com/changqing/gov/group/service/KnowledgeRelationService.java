package com.changqing.gov.group.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.group.dto.KnowledgeRelationPageDTO;
import com.changqing.gov.group.entity.KnowledgeRelation;

/**
 * <p>
 * 业务接口
 * 作业关联配置表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
public interface KnowledgeRelationService extends SuperService<KnowledgeRelation> {
    IPage<KnowledgeRelation> getRelationPage(PageParams<KnowledgeRelationPageDTO> params);
}
