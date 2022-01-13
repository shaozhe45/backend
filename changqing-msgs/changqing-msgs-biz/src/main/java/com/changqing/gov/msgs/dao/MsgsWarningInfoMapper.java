package com.changqing.gov.msgs.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.msgs.dto.MsgsWarningInfoDTO;
import com.changqing.gov.msgs.entity.MsgsWarningInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author ：Angular
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.dao
 * @InterfaceName: MsgsWarningInfoMapper
 * @date ：Created in 2021/1/8 14:26
 * @description：待处理警告数据链路mapper接口
 * @modified By：
 * @version: v1.0.0$
 */
@Repository
public interface MsgsWarningInfoMapper extends SuperMapper<MsgsWarningInfo> {

    /**
     * 分页查询待处理告警业务逻辑
     * @param page
     * @param data
     * @return
     */
    IPage<MsgsWarningInfo>  queryResult(IPage<MsgsWarningInfo> page, @Param("data") MsgsWarningInfoDTO data);

    String selectWarningCode(@Param("warningCode")String warningCode,@Param("receivier") String receivier);

    int isDeal(@Param("warningCode")String warningCode,@Param("receivier") String receivier,@Param("isDeal") String isDeal, @Param("updateUser")String updateUser, @Param("updateTime")String updateTime);

    int insertMsgContent(@Param("warningCode")String warningCode, @Param("warningContent")String warningContent, @Param("warningTime")String warningTime);
}
