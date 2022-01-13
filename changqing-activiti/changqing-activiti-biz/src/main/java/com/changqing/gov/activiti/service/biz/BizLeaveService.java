package com.changqing.gov.activiti.service.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.domain.core.UpdateCollEntity;
import com.changqing.gov.activiti.dto.biz.BizLeavePageDTO;
import com.changqing.gov.activiti.dto.biz.BizLeaveResDTO;
import com.changqing.gov.activiti.entity.biz.BizLeave;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 请假流程
 * </p>
 *
 * @author wz
 * @date 2020-08-12
 */
public interface BizLeaveService extends SuperService<BizLeave> {

    String getKey();
    /**
     * 保存业务实体
     *
     * @param bizLeave 业务实体
     * @return
     */
    Boolean saveBiz(BizLeave bizLeave);

    /**
     * 删除业务实体
     *
     * @param entity 标识集体修改实体
     * @return
     */
    Boolean deleteBiz(UpdateCollEntity<String> entity);

    /**
     * 分页查询业务实体
     *
     * @param params 分页入参
     * @return
     */
    IPage<BizLeaveResDTO> pageBiz(PageParams<BizLeavePageDTO> params);

}
