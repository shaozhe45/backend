package com.changqing.gov.group.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.changqing.gov.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_knowledge_relation")
@ApiModel(value = "KnowledgeRelation", description = "作业关联配置表")
@AllArgsConstructor
public class KnowledgeRelation extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 关联类型
     */
    @ApiModelProperty(value = "关联类型")
    @NotEmpty(message = "关联类型不能为空")
    @Length(max = 255, message = "关联类型长度不能超过255")
    @TableField(value = "relation_type", condition = LIKE)
    @Excel(name = "关联类型")
    private String relationType;

    /**
     * 关联内容
     */
    @ApiModelProperty(value = "关联内容")
    @NotEmpty(message = "关联内容不能为空")
    @Length(max = 255, message = "关联内容长度不能超过255")
    @TableField(value = "relation_content", condition = LIKE)
    @Excel(name = "关联内容")
    private String relationContent;

    /**
     * 知识类型ID
     */
    @ApiModelProperty(value = "知识类型ID")
    @NotNull(message = "知识类型ID不能为空")
    @TableField("type_id")
    @Excel(name = "知识类型ID")
    private Long typeId;

    /**
     * 知识类型
     */
    @ApiModelProperty(value = "知识类型")
    @TableField(exist = false)
    private String type;

    /**
     * 知识名称ID
     */
    @ApiModelProperty(value = "知识名称ID")
    @NotNull(message = "知识名称ID不能为空")
    @TableField("name_id")
    @Excel(name = "知识名称ID")
    private Long nameId;

    /**
     * 知识名称
     */
    @ApiModelProperty(value = "知识名称")
    @TableField(exist = false)
    private String name;

    /**
     * 是否启用，0未启用，1启用
     */
    @ApiModelProperty(value = "是否启用，0未启用，1启用")
    @TableField("status")
    @Excel(name = "是否启用，0未启用，1启用", replace = {"是_true", "否_false", "_null"})
    private Boolean status;


    @Builder
    public KnowledgeRelation(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, 
                    String relationType, String relationContent, Long typeId, Long nameId, Boolean status) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.relationType = relationType;
        this.relationContent = relationContent;
        this.typeId = typeId;
        this.nameId = nameId;
        this.status = status;
    }

}
