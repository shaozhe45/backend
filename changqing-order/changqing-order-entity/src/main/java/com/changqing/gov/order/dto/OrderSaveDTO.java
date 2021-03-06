package com.changqing.gov.order.dto;

import com.changqing.gov.common.constant.DictionaryType;
import com.changqing.gov.injection.annonation.InjectionField;
import com.changqing.gov.model.RemoteData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.changqing.gov.common.constant.InjectionFieldConstants.DICTIONARY_ITEM_FEIGN_CLASS;
import static com.changqing.gov.common.constant.InjectionFieldConstants.DICTIONARY_ITEM_METHOD;
import static com.changqing.gov.common.constant.InjectionFieldConstants.ORG_ID_FEIGN_CLASS;
import static com.changqing.gov.common.constant.InjectionFieldConstants.ORG_ID_NAME_METHOD;

/**
 * <p>
 * 实体类
 * 订单(用于测试)
 * </p>
 *
 * @author changqing
 * @since 2020-06-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "OrderSaveDTO", description = "订单(用于测试)")
public class OrderSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 255, message = "名称长度不能超过255")
    private String name;
    /**
     * 学历
     *
     * @InjectionField(api = "orderServiceImpl", method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.EDUCATION) RemoteData<String, String>
     */
    @ApiModelProperty(value = "学历")
    @Length(max = 255, message = "学历长度不能超过255")
    @InjectionField(api = "orderServiceImpl", method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.EDUCATION)
    private RemoteData<String, String> education;
    /**
     * 民族
     *
     * @InjectionField(api = DICTIONARY_ITEM_FEIGN_CLASS, method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.NATION) RemoteData<String, String>
     */
    @ApiModelProperty(value = "民族")
    @Length(max = 255, message = "民族长度不能超过255")
    @InjectionField(api = DICTIONARY_ITEM_FEIGN_CLASS, method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.NATION)
    private RemoteData<String, String> nation;
    /**
     * 组织ID
     * #c_core_org
     *
     * @InjectionField(api = ORG_ID_FEIGN_CLASS, method = ORG_ID_NAME_METHOD) RemoteData<Long, String>
     */
    @ApiModelProperty(value = "组织ID")
    @InjectionField(api = ORG_ID_FEIGN_CLASS, method = ORG_ID_NAME_METHOD)
    private RemoteData<Long, String> org;
    /**
     * 编号
     */
    @ApiModelProperty(value = "编号")
    @Length(max = 255, message = "编号长度不能超过255")
    private String code;

}
