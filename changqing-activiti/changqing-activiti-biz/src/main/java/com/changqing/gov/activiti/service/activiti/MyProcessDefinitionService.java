package com.changqing.gov.activiti.service.activiti;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.utils.ArgumentAssert;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.changqing.gov.activiti.domain.activiti.ProcessDefinitionDO;
import com.changqing.gov.activiti.exception.MyActivitiExceptionCode;
import com.changqing.gov.activiti.exception.MyException;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.utils.BizAssert;
import com.changqing.gov.utils.StrHelper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * ??????????????????
 *
 * @author wz
 * @date 2020-08-07
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
@RequiredArgsConstructor
public class MyProcessDefinitionService {

    private final RepositoryService repositoryService;

    /**
     * ??????????????????????????????
     *
     * @param pageParams ????????????
     */
    @Transactional(readOnly = true)
    public IPage<ProcessDefinitionDO> listProcessDefinition(PageParams<ProcessDefinitionDO> pageParams) {
        IPage<ProcessDefinitionDO> page = pageParams.buildPage().setRecords(new ArrayList());
        ProcessDefinitionDO processDefinitionDO = pageParams.getModel();

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.orderByProcessDefinitionId().desc().orderByProcessDefinitionVersion().desc();
        if (StrUtil.isNotBlank(processDefinitionDO.getName())) {
            processDefinitionQuery.processDefinitionNameLike(StrHelper.fullLike(processDefinitionDO.getName()));
        }
        if (StrUtil.isNotBlank(processDefinitionDO.getKey())) {
            processDefinitionQuery.processDefinitionKeyLike(StrHelper.fullLike(processDefinitionDO.getKey()));
        }
        if (StrUtil.isNotBlank(processDefinitionDO.getCategory())) {
            processDefinitionQuery.processDefinitionCategoryLike(StrHelper.fullLike(processDefinitionDO.getCategory()));
        }
        if (StrUtil.isNotEmpty(BaseContextHandler.getTenant())) {
            processDefinitionQuery.processDefinitionTenantId(BaseContextHandler.getTenant());
        }

        page.setTotal(processDefinitionQuery.count());
        if (page.getTotal() > 0) {
            List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage((int) page.offset(), (int) page.getSize());

            for (ProcessDefinition definition : processDefinitionList) {
                ProcessDefinitionEntityImpl entityImpl = (ProcessDefinitionEntityImpl) definition;
                ProcessDefinitionDO entity = BeanUtil.toBean(definition, ProcessDefinitionDO.class);
                DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery().deploymentId(definition.getDeploymentId());
                if (StrUtil.isNotEmpty(BaseContextHandler.getTenant())) {
                    deploymentQuery.deploymentTenantId(BaseContextHandler.getTenant());
                }
                Deployment deployment = deploymentQuery.singleResult();
                entity.setDeploymentId(deployment.getId());
                entity.setDeploymentTime(deployment.getDeploymentTime());
                entity.setDeploymentName(deployment.getName());
                entity.setSuspendState(String.valueOf(entityImpl.getSuspensionState()));
                if (entityImpl.getSuspensionState() == 1) {
                    entity.setSuspendStateName("?????????");
                } else {
                    entity.setSuspendStateName("?????????");
                }
                page.getRecords().add(entity);
            }
        }
        return page;
    }

    /**
     * ??????????????????
     *
     * @param deploymentIdsArr ??????id??????
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProcessDeploymentByIds(List<String> deploymentIdsArr) {
        for (String deploymentId : deploymentIdsArr) {
            repositoryService.deleteDeployment(deploymentId, true);
        }
        return true;
    }

    /**
     * ????????????????????????
     *
     * @param processDefinitionId ????????????ID
     * @param suspendState        ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public void suspendOrActiveApply(String processDefinitionId, String suspendState) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        query.processDefinitionId(processDefinitionId);
        if (StrUtil.isNotEmpty(BaseContextHandler.getTenant())) {
            query.processDefinitionTenantId(BaseContextHandler.getTenant());
        }
        ProcessDefinition processDefinition = query.singleResult();
        if (processDefinition == null) {
            MyException.exception(MyActivitiExceptionCode.LOGIN_HAVE_NOT_AUTH);
        }

        if ("1".equals(suspendState)) {
            repositoryService.suspendProcessDefinitionById(processDefinitionId);
        } else if ("2".equals(suspendState)) {
            repositoryService.activateProcessDefinitionById(processDefinitionId);
        }
    }

    /**
     * ??????ZIP??????????????????
     *
     * @param name ?????????
     * @param path ??????
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public String deploymentDefinitionByZip(String name, String path) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(path)))) {
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            Deployment deployment = deploymentBuilder
                    .tenantId(BaseContextHandler.getTenant())
                    .addZipInputStream(zis)
                    .name(name)
                    //????????????DeploymentBuilder isDuplicateFilterEnabled ??????????????? true, ?????????????????????????????????????????????????????????????????????????????????????????????????????????
//                    .enableDuplicateFiltering()
                    .deploy();
            return deployment.getId();
        }

    }

    /**
     * ??????????????????????????????
     *
     * @param processDefinitionId ????????????id
     */
    @Transactional(rollbackFor = Exception.class)
    public Model saveModelByPro(String processDefinitionId) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.processDefinitionId(processDefinitionId);
        if (StrUtil.isNotEmpty(BaseContextHandler.getTenant())) {
            processDefinitionQuery.processDefinitionTenantId(BaseContextHandler.getTenant());
        }

        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
        ArgumentAssert.notNull(processDefinition, "?????????????????????");
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in;
        XMLStreamReader xtr = null;
        try {
            in = new InputStreamReader(bpmnStream, StandardCharsets.UTF_8);
            xtr = xif.createXMLStreamReader(in);
        } catch (XMLStreamException e) {
            MyException.exception(MyActivitiExceptionCode.FILE_LOAD_ERR);
        }
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
        BpmnJsonConverter converter = new BpmnJsonConverter();
        ObjectNode modelNode = converter.convertToJson(bpmnModel);

        Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setCategory(processDefinition.getCategory());
        modelData.setName(processDefinition.getName() + "(??????)");
        modelData.setTenantId(BaseContextHandler.getTenant());
        modelData.setDeploymentId(processDefinition.getDeploymentId());
        ModelQuery modelQuery = repositoryService.createModelQuery().modelKey(modelData.getKey());
        if (StrUtil.isNotEmpty(BaseContextHandler.getTenant())) {
            modelQuery.modelTenantId(BaseContextHandler.getTenant());
        }
        long keyCount = modelQuery.count();
        modelData.setVersion(Convert.toInt(keyCount + 1));

        JSONObject meta = JSONUtil.createObj();
        meta.set(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        meta.set(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
        meta.set(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(meta.toString());

        repositoryService.saveModel(modelData);
        repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes(StandardCharsets.UTF_8));

        return modelData;
    }

    public List<ProcessDefinitionDO> getDefVerHistory(String key) {
        List<ProcessDefinitionDO> res = new ArrayList<>();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).list();
        for (ProcessDefinition definition : list) {
            ProcessDefinitionEntityImpl entityImpl = (ProcessDefinitionEntityImpl) definition;
            ProcessDefinitionDO entity = BeanUtil.toBean(definition, ProcessDefinitionDO.class);
            entity.setDeploymentId(definition.getDeploymentId());
            entity.setKey(definition.getKey());
            entity.setVersion(definition.getVersion());
            entity.setSuspendState(String.valueOf(entityImpl.getSuspensionState()));
            if (entityImpl.getSuspensionState() == 1) {
                entity.setSuspendStateName("?????????");
            } else {
                entity.setSuspendStateName("?????????");
            }
            res.add(entity);
        }

        return res;
    }
}
