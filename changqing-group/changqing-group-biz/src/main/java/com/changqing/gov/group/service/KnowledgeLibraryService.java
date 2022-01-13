package com.changqing.gov.group.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.group.dto.KnowledgeLibraryPageDTO;
import com.changqing.gov.group.entity.KnowledgeLibrary;

import java.util.Map;

/**
 * <p>
 * 业务接口
 * 知识库表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
public interface KnowledgeLibraryService extends SuperService<KnowledgeLibrary> {
    IPage<KnowledgeLibrary> getLibraryPage(PageParams<KnowledgeLibraryPageDTO> params);
}
