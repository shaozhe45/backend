package com.changqing.gov.group.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.group.entity.KnowledgeLibrary;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 知识库表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Repository
public interface KnowledgeLibraryMapper extends SuperMapper<KnowledgeLibrary> {
    IPage<KnowledgeLibrary> getLibraryPage(IPage<KnowledgeLibrary> page, @Param("model") KnowledgeLibrary model);
}
