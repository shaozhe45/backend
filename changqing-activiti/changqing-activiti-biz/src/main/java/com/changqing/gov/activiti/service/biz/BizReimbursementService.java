package com.changqing.gov.activiti.service.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.domain.core.UpdateCollEntity;
import com.changqing.gov.activiti.dto.biz.BizReimbursementPageDTO;
import com.changqing.gov.activiti.dto.biz.BizReimbursementResDTO;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.activiti.entity.biz.BizReimbursement;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 业务接口
 * 报销流程
 * </p>
 *
 * @author wz
 * @date 2020-08-31
 */
public interface BizReimbursementService extends SuperService<BizReimbursement> {

    String getKey();

    @Transactional(rollbackFor = Exception.class)
    Boolean saveBiz(BizReimbursement bizReimbursement);

    @Transactional(rollbackFor = Exception.class)
    Boolean deleteBiz(UpdateCollEntity<String> entity);

    @Transactional(rollbackFor = Exception.class)
    IPage<BizReimbursementResDTO> pageBiz(PageParams<BizReimbursementPageDTO> params);
}
