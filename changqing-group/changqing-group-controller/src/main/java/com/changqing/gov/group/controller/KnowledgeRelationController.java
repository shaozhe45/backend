package com.changqing.gov.group.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.group.dto.KnowledgeRelationPageDTO;
import com.changqing.gov.group.dto.KnowledgeRelationSaveDTO;
import com.changqing.gov.group.dto.KnowledgeRelationUpdateDTO;
import com.changqing.gov.group.entity.KnowledgeRelation;
import com.changqing.gov.group.service.KnowledgeRelationService;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 作业关联配置表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeRelation")
@Api(value = "KnowledgeRelation", tags = "作业关联配置表")
// @PreAuth(replace = "knowledgeRelation:")
public class KnowledgeRelationController extends SuperController<KnowledgeRelationService, Long, KnowledgeRelation, KnowledgeRelationPageDTO, KnowledgeRelationSaveDTO, KnowledgeRelationUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<KnowledgeRelation> knowledgeRelationList = list.stream().map((map) -> {
            KnowledgeRelation knowledgeRelation = KnowledgeRelation.builder().build();
            //TODO 请在这里完成转换
            return knowledgeRelation;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(knowledgeRelationList));
    }

    @Override
    public R<IPage<KnowledgeRelation>> page(PageParams<KnowledgeRelationPageDTO> params) {
        return R.success(baseService.getRelationPage(params));
    }
}
