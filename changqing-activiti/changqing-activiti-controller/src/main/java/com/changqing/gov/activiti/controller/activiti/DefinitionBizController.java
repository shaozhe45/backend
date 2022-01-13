package com.changqing.gov.activiti.controller.activiti;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.constant.PathProperties;
import com.changqing.gov.activiti.domain.activiti.ProcessDefinitionDO;
import com.changqing.gov.activiti.service.activiti.MyProcessDefinitionService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.context.BaseContextHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static com.changqing.gov.base.R.fail;
import static com.changqing.gov.base.R.success;

/**
 * 流程定义管理
 *
 * @author wz
 * @date 2020-08-07
 */
@Slf4j
@RestController
@RequestMapping("definition")
@AllArgsConstructor
public class DefinitionBizController {
    @Autowired
    private final MyProcessDefinitionService myProcessDefinitionService;
    @Autowired
    private final PathProperties pathProperties;

    /**
     * 模型分页查询
     *
     * @param dto 分页查询实体
     * @return
     */
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public R<IPage<ProcessDefinitionDO>> page(@RequestBody PageParams<ProcessDefinitionDO> dto) {
        IPage<ProcessDefinitionDO> processDefinitionDOIPage = myProcessDefinitionService.listProcessDefinition(dto);
        return success(processDefinitionDOIPage);
    }

    /**
     * 删除流程实例及模型
     *
     * @param deploymentIds
     * @return
     */
    @DeleteMapping(value = "/delete")
    public R<Boolean> delete(@RequestParam(value = "deploymentIds[]") List<String> deploymentIds) {
        return R.success(myProcessDefinitionService.deleteProcessDeploymentByIds(deploymentIds));
    }

    /**
     * 修改流程状态
     *
     * @param processDefinitionId 流程定义id
     * @param suspendState        修改状态
     * @return
     */
    @GetMapping(value = "/updateSuspendState")
    public R<Boolean> updateSuspendState(@RequestParam(value = "id") String processDefinitionId, @RequestParam(value = "suspendState") String suspendState) {
        myProcessDefinitionService.suspendOrActiveApply(processDefinitionId, suspendState);
        return success(true);
    }

    /**
     * 导入流程模型
     *
     * @param file 上传文件
     * @return
     */
    @SneakyThrows
    @PostMapping(value = "/upload")
    public R<String> upload(@RequestParam(value = "file", required = false) MultipartFile file,@RequestParam(value = "defName") String defName) {
        if (file == null || file.isEmpty()) {
            return fail("文件为空!");
        }
        String filePath = pathProperties.getUploadPath();
        String tempStoragePath = Paths.get(filePath, BaseContextHandler.getTenant(), UUID.fastUUID().toString(true) + ".zip").toString();
        File desc = new File(tempStoragePath);
        FileUtil.writeFromStream(file.getInputStream(), desc);

        String id = myProcessDefinitionService.deploymentDefinitionByZip(defName, tempStoragePath);
        return success(id);

    }


    /**
     * 通过流程定义映射模型
     *
     * @param processDefinitionId 流程id
     * @return
     */
    @GetMapping(value = "/saveModelByPro")
    public R<Model> saveModelByPro(@RequestParam(value = "processDefinitionId") String processDefinitionId) {
        Model model = myProcessDefinitionService.saveModelByPro(processDefinitionId);
        return success(model);
    }
}
