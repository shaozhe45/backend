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
 * 知识库表
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
@ApiModel(value = "KnowledgeLibraryPageDTO", description = "知识库表")
public class KnowledgeLibraryPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 知识名称
     */
    @ApiModelProperty(value = "知识名称")
    @NotEmpty(message = "知识名称不能为空")
    @Length(max = 255, message = "知识名称长度不能超过255")
    private String name;
    /**
     * 知识类型ID
     */
    @ApiModelProperty(value = "知识类型ID")
    @NotNull(message = "知识类型ID不能为空")
    private Long typeId;
    /**
     * 负责人ID
     */
    @ApiModelProperty(value = "负责人ID")
    @NotNull(message = "负责人ID不能为空")
    private Long managerId;
    /**
     * 是否启用，0未启用，1启用
     */
    @ApiModelProperty(value = "是否启用，0未启用，1启用")
    private Boolean status;

}
