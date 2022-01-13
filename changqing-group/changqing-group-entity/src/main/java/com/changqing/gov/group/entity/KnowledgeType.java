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
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_knowledge_type")
@ApiModel(value = "KnowledgeType", description = "知识类型字典表")
@AllArgsConstructor
public class KnowledgeType extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 知识类型
     */
    @ApiModelProperty(value = "知识类型")
    @NotEmpty(message = "知识类型不能为空")
    @Length(max = 50, message = "知识类型长度不能超过50")
    @TableField(value = "type", condition = LIKE)
    @Excel(name = "知识类型")
    private String type;


    @Builder
    public KnowledgeType(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, 
                    String type) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.type = type;
    }

}
