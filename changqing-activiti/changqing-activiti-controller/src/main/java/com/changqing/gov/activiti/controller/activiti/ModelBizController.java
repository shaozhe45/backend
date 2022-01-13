package com.changqing.gov.activiti.controller.activiti;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.dto.activiti.ModelPublishReqDTO;
import com.changqing.gov.activiti.dto.activiti.ModelSelectReqDTO;
import com.changqing.gov.activiti.exception.MyActivitiExceptionCode;
import com.changqing.gov.activiti.exception.MyException;
import com.changqing.gov.activiti.service.activiti.MyModelService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.request.PageParams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 模型管理
 *
 * @author wz
 * @date 2020-08-07
 */
@Slf4j
@RestController
@RequestMapping("model")
@AllArgsConstructor
public class ModelBizController {
    private final MyModelService myModelService;

    /**
     * 模型分页查询
     *
     * @param dto 查询实体
     * @return
     */
    @RequestMapping(value = "/pageModel", method = RequestMethod.POST)
    public R<IPage<Model>> pageModel(@RequestBody PageParams<ModelSelectReqDTO> dto) {
        IPage<Model> page = myModelService.pageModel(dto);
        return R.success(page);
    }

    /**
     * 创建模型
     *
     * @param name 模型名称
     * @param key  模型key
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public R<String> create(String name, String key, String description) {
        if (StringUtils.isEmpty(name)) {
            MyException.exception(MyActivitiExceptionCode.MODEL_NAME_NONE);
        }
        if (StringUtils.isEmpty(key)) {
            MyException.exception(MyActivitiExceptionCode.MODEL_KEY_NONE);
        }

        Model model = myModelService.create(name, key, description);
        return R.success(model.getId());
    }


    /**
     * 删除流程实例及模型
     *
     * @param modelId 模型ID
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public R<Boolean> deleteProcessInstance(String modelId) {
        return R.success(myModelService.deleteModel(modelId));
    }

    /**
     * 发布流程
     *
     * @param dto 模型
     * @return
     */
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public R<Map<String, String>> publish(@RequestBody ModelPublishReqDTO dto) {
        return R.success(myModelService.publish(dto));
    }
}
