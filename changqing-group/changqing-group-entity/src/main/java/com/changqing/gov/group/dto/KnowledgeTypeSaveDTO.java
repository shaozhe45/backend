package com.changqing.gov.group.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 知识类型字典表
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
@ApiModel(value = "KnowledgeTypeSaveDTO", description = "知识类型字典表")
public class KnowledgeTypeSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 知识类型
     */
    @ApiModelProperty(value = "知识类型")
    @NotEmpty(message = "知识类型不能为空")
    @Length(max = 50, message = "知识类型长度不能超过50")
    private String type;

}
