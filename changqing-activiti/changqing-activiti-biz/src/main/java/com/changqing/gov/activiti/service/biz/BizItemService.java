package com.changqing.gov.activiti.service.biz;

import com.changqing.gov.activiti.dto.biz.BizItemResDTO;
import com.changqing.gov.activiti.entity.biz.BizItem;
import com.changqing.gov.base.service.SuperService;

import java.util.List;

/**
 * <p>
 * 业务接口
 * <p>
 * </p>
 *
 * @author wz
 * @date 2020-08-19
 */
public interface BizItemService extends SuperService<BizItem> {

    /**
     * 保存实体
     *
     * @param po 实体
     * @return
     */
    boolean saveItem(BizItem po);

    /**
     * 查询实体
     *
     * @param instId 实例id
     * @return
     */
    List<BizItemResDTO> find(String instId);
}
