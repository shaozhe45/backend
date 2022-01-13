package com.changqing.gov.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.group.dao.KnowledgeLibraryMapper;
import com.changqing.gov.group.dto.KnowledgeLibraryPageDTO;
import com.changqing.gov.group.entity.KnowledgeLibrary;
import com.changqing.gov.group.service.KnowledgeLibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 业务实现类
 * 知识库表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class KnowledgeLibraryServiceImpl extends SuperServiceImpl<KnowledgeLibraryMapper, KnowledgeLibrary> implements KnowledgeLibraryService {
    @Override
    public IPage<KnowledgeLibrary> getLibraryPage(PageParams<KnowledgeLibraryPageDTO> params) {
        IPage<KnowledgeLibrary> page = params.buildPage();
        KnowledgeLibrary model = BeanUtil.toBean(params.getModel(), KnowledgeLibrary.class);
        IPage<KnowledgeLibrary> libraryPage = baseMapper.getLibraryPage(page, model);
        return libraryPage;
    }
}
