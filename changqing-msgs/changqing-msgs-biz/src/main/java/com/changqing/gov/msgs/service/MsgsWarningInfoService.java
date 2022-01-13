package com.changqing.gov.msgs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.msgs.dto.MsgsWarningInfoDTO;
import com.changqing.gov.msgs.entity.MsgsWarningInfo;

import java.util.List;

/**
 * @author ：Angular
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.service
 * @InterfaceName: MsgsWarningInfoService
 * @date ：Created in 2021/1/8 14:22
 * @description：待处理告警业务逻辑接口
 * @modified By：
 * @version: v1.0.0$
 */
public interface MsgsWarningInfoService extends SuperService<MsgsWarningInfo> {

    /**
     * 分页查询待处理告警业务逻辑
     * @param page
     * @param data
     * @return
     */
    IPage<MsgsWarningInfo> queryResult(IPage<MsgsWarningInfo> page, MsgsWarningInfoDTO data);

    String selectWarningCode(String warningCode,String receivier);

    boolean isDeal(String warningCode, String receivier, String isCheck, String updateUser, String updateTime);

    int insertMsgContent(String warningCode, String warningContent, String warningTime);
}
