package com.changqing.gov.group.controller;

import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.database.mybatis.conditions.query.QueryWrap;
import com.changqing.gov.group.dto.KnowledgeTypePageDTO;
import com.changqing.gov.group.dto.KnowledgeTypeSaveDTO;
import com.changqing.gov.group.dto.KnowledgeTypeUpdateDTO;
import com.changqing.gov.group.entity.KnowledgeLibrary;
import com.changqing.gov.group.entity.KnowledgeType;
import com.changqing.gov.group.service.KnowledgeLibraryService;
import com.changqing.gov.group.service.KnowledgeTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 知识类型字典表
 * </p>
 *
 * @author wshaozhe
 * @date 2022-01-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeType")
@Api(value = "KnowledgeType", tags = "知识类型字典表")
// @PreAuth(replace = "knowledgeType:")
public class KnowledgeTypeController extends SuperController<KnowledgeTypeService, Long, KnowledgeType, KnowledgeTypePageDTO, KnowledgeTypeSaveDTO, KnowledgeTypeUpdateDTO> {
    @Autowired
    private KnowledgeLibraryService knowledgeLibraryService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<KnowledgeType> knowledgeTypeList = list.stream().map((map) -> {
            KnowledgeType knowledgeType = KnowledgeType.builder().build();
            //TODO 请在这里完成转换
            return knowledgeType;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(knowledgeTypeList));
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> longs) {
        QueryWrap<KnowledgeLibrary> queryWrap = new QueryWrap<>();
        queryWrap.eq("type_id", longs.get(0));
        Integer integer = knowledgeLibraryService.count(queryWrap);
        return integer > 0 ? R.fail("该知识库类型下知识库数量不为0，不能进行删除操作") : R.successDef();
    }

    @ApiOperation(value = "查询所有知识类型", notes = "查询所有知识类型")
    @GetMapping("getList")
    public R<Map<String,Object>> getList(){
        QueryWrap<KnowledgeType> queryWrap = new QueryWrap<>();
        queryWrap.select("id as type_id","type");
        return R.success(baseService.getMap(queryWrap));
    }
}
