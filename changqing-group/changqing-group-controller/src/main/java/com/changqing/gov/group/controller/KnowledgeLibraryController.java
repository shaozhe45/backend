package com.changqing.gov.group.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.database.mybatis.conditions.query.QueryWrap;
import com.changqing.gov.group.dto.KnowledgeLibraryPageDTO;
import com.changqing.gov.group.dto.KnowledgeLibrarySaveDTO;
import com.changqing.gov.group.dto.KnowledgeLibraryUpdateDTO;
import com.changqing.gov.group.entity.KnowledgeLibrary;
import com.changqing.gov.group.service.KnowledgeLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 知识库表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeLibrary")
@Api(value = "KnowledgeLibrary", tags = "知识库表")
// @PreAuth(replace = "knowledgeLibrary:")
public class KnowledgeLibraryController extends SuperController<KnowledgeLibraryService, Long, KnowledgeLibrary, KnowledgeLibraryPageDTO, KnowledgeLibrarySaveDTO, KnowledgeLibraryUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<KnowledgeLibrary> knowledgeLibraryList = list.stream().map((map) -> {
            KnowledgeLibrary knowledgeLibrary = KnowledgeLibrary.builder().build();
            //TODO 请在这里完成转换
            return knowledgeLibrary;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(knowledgeLibraryList));
    }

    @Override
    public R<IPage<KnowledgeLibrary>> page(PageParams<KnowledgeLibraryPageDTO> params) {
        return R.success(baseService.getLibraryPage(params));
    }

    @ApiOperation(value = "根据类型Id查询所有启用状态的知识库名称", notes = "根据类型Id查询所有启用状态的知识库名称")
    @PostMapping("getList")
    public R<Map<String,Object>> getList(@RequestParam("typeId") String typeId){
        QueryWrap<KnowledgeLibrary> queryWrap = new QueryWrap<>();
        queryWrap.select("id as name_id","name").eq("type_id",typeId).eq("status","1");
        return R.success(baseService.getMap(queryWrap));
    }
}
