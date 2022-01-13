package com.changqing.gov.activiti.dto.activiti;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.activiti.engine.identity.User;

import java.util.List;

/**
 * 流程模型发布入参
 *
 * @author wz
 * @date 2020-08-07
 */
@Data
@ApiModel(value = "ModelPublishReqDTO", description = "流程模型发布入参")
public class ModelPublishReqDTO {
    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    private String modelId;
}
