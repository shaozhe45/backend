package com.changqing.gov;

import com.baomidou.mybatisplus.annotation.TableField;
import com.changqing.gov.authority.entity.core.Org;
import com.changqing.gov.common.constant.DictionaryType;
import com.changqing.gov.injection.annonation.InjectionField;
import com.changqing.gov.model.RemoteData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

import static com.changqing.gov.common.constant.InjectionFieldConstants.STATION_ID_METHOD;

@Data
@ToString
/**
 * 测试DTO
 *
 * @author changqing
 * @date 2019/07/25
 */
public class TestModel {
    private LocalDateTime date;
    private Date d2;

    @ApiModelProperty(value = "组织ID")
    @TableField("org_id")
//    @InjectionField(feign = OrgApi.class, method = ORG_ID_METHOD)
    private RemoteData<Long, Org> org;


    //    @InjectionField(api = "orgApi", method = "findOrgNameByIds")
    private RemoteData<Long, String> org2;


    @InjectionField(apiClass = Object.class, method = "findOrgNameByIds")
    private RemoteData<Long, String> error;

    @InjectionField(api = "stationServiceImpl", method = STATION_ID_METHOD)
    private RemoteData<Long, Org> station;

    // 去数据字典表 根据code 查询 name
    @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem", dictType = DictionaryType.EDUCATION)
    private RemoteData<String, String> education;

    @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem", dictType = DictionaryType.EDUCATION)
    private String education2;

}
