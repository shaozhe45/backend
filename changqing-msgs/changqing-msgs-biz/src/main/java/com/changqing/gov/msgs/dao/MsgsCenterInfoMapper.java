package com.changqing.gov.msgs.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.msgs.dto.MsgsCenterInfoPageResultDTO;
import com.changqing.gov.msgs.dto.MsgsCenterInfoQueryDTO;
import com.changqing.gov.msgs.entity.MsgsCenterInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 消息中心
 * </p>
 *
 * @author changqing
 * @date 2019-08-01
 */
@Repository
public interface MsgsCenterInfoMapper extends SuperMapper<MsgsCenterInfo> {
    /**
     * 查询消息中心分页数据
     *
     * @param page
     * @param data
     * @return
     */
    IPage<MsgsCenterInfoPageResultDTO> page(IPage page, @Param("data") MsgsCenterInfoQueryDTO data);

    /**
     * 查询统计待审核信息
     * @return
     */
    int queryUnAudit();

    /**
     * 数据审核操作
     * @param isCheck 是否审核通过 0不通过 1通过 2待审核
     * @param checkTime 审核时间
     * @return
     */
    int isChecked(@Param("params") Map<String,Object> params);

    int queryUnDeal();

    int queryUnRead();
}
