package com.changqing.gov.group.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.changqing.gov.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_knowledge_library")
@ApiModel(value = "KnowledgeLibrary", description = "知识库表")
@AllArgsConstructor
public class KnowledgeLibrary extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 知识名称
     */
    @ApiModelProperty(value = "知识名称")
    @NotEmpty(message = "知识名称不能为空")
    @Length(max = 255, message = "知识名称长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "知识名称")
    private String name;

    /**
     * 知识类型ID
     */
    @ApiModelProperty(value = "知识类型ID")
    @NotNull(message = "知识类型ID不能为空")
    @TableField("type_id")
    @Excel(name = "知识类型ID")
    private Long typeId;

    /**
     * 负责人ID
     */
    @ApiModelProperty(value = "负责人ID")
    @NotNull(message = "负责人ID不能为空")
    @TableField("manager_id")
    @Excel(name = "负责人ID")
    private Long managerId;

    /**
     * 负责人姓名
     */
    @ApiModelProperty(value = "负责人姓名")
    @TableField(exist = false)
    private String managerName;

    /**
     * 负责人电话
     */
    @ApiModelProperty(value = "负责人姓名")
    @TableField(exist = false)
    private String mobile;

    /**
     * 是否启用，0未启用，1启用
     */
    @ApiModelProperty(value = "是否启用，0未启用，1启用")
    @TableField("status")
    @Excel(name = "是否启用，0未启用，1启用", replace = {"是_true", "否_false", "_null"})
    private Boolean status;


    @Builder
    public KnowledgeLibrary(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, 
                    String name, Long typeId, Long managerId, Boolean status) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.name = name;
        this.typeId = typeId;
        this.managerId = managerId;
        this.status = status;
    }

}
