package com.changqing.gov.group.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 作业关联配置表
 * </p>
 *
 * @author wshaozhe
 * @since 2022-01-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeRelationSaveDTO", description = "作业关联配置表")
public class KnowledgeRelationSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联类型
     */
    @ApiModelProperty(value = "关联类型")
    @NotEmpty(message = "关联类型不能为空")
    @Length(max = 255, message = "关联类型长度不能超过255")
    private String relationType;
    /**
     * 关联内容
     */
    @ApiModelProperty(value = "关联内容")
    @NotEmpty(message = "关联内容不能为空")
    @Length(max = 255, message = "关联内容长度不能超过255")
    private String relationContent;
    /**
     * 知识类型ID
     */
    @ApiModelProperty(value = "知识类型ID")
    @NotNull(message = "知识类型ID不能为空")
    private Long typeId;
    /**
     * 知识名称ID
     */
    @ApiModelProperty(value = "知识名称ID")
    @NotNull(message = "知识名称ID不能为空")
    private Long nameId;
    /**
     * 是否启用，0未启用，1启用
     */
    @ApiModelProperty(value = "是否启用，0未启用，1启用")
    private Boolean status;

}
