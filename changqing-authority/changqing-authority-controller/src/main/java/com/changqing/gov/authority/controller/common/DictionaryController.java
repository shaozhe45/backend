package com.changqing.gov.authority.controller.common;


import com.changqing.gov.authority.dto.common.DictionarySaveDTO;
import com.changqing.gov.authority.dto.common.DictionaryUpdateDTO;
import com.changqing.gov.authority.entity.common.Dictionary;
import com.changqing.gov.authority.entity.common.DictionaryItem;
import com.changqing.gov.authority.service.common.DictionaryItemService;
import com.changqing.gov.authority.service.common.DictionaryService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * 字典类型
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dictionary")
@Api(value = "Dictionary", tags = "字典类型")
@PreAuth(replace = "dict:")
public class DictionaryController extends SuperController<DictionaryService, Long, Dictionary, Dictionary, DictionarySaveDTO, DictionaryUpdateDTO> {

    @Autowired
    private DictionaryItemService dictionaryItemService;

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        this.baseService.removeByIds(ids);
        this.dictionaryItemService.remove(Wraps.<DictionaryItem>lbQ().in(DictionaryItem::getDictionaryId, ids));
        return this.success(true);
    }
}
