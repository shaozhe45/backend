package com.changqing.gov.group.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.group.entity.KnowledgeLibrary;
import com.changqing.gov.group.entity.KnowledgeRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 作业关联配置表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Repository
public interface KnowledgeRelationMapper extends SuperMapper<KnowledgeRelation> {
    IPage<KnowledgeLibrary> getRelationPage(IPage<KnowledgeRelation> page, @Param("model") KnowledgeRelation model);
}
