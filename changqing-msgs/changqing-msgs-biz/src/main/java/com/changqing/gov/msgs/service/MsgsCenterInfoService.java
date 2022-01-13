package com.changqing.gov.msgs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.msgs.dto.MsgsCenterInfoPageResultDTO;
import com.changqing.gov.msgs.dto.MsgsCenterInfoQueryDTO;
import com.changqing.gov.msgs.dto.MsgsCenterInfoSaveDTO;
import com.changqing.gov.msgs.entity.MsgsCenterInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 消息中心
 * </p>
 *
 * @author changqing
 * @date 2019-08-01
 */
public interface MsgsCenterInfoService extends SuperService<MsgsCenterInfo> {

    /**
     * 保存消息
     *
     * @param data
     * @return
     */
    MsgsCenterInfo saveMsgs(MsgsCenterInfoSaveDTO data);

    /**
     * 删除指定用户 指定消息的数据
     *
     * @param ids
     * @param userId
     * @return
     */
    boolean delete(List<Long> ids, Long userId);

    /**
     * 标记状态
     *
     * @param msgCenterIds 主表id
     * @param userId       用户id
     * @return
     */
    boolean mark(List<Long> msgCenterIds, Long userId);

    /**
     * 分页查询
     *
     * @param page
     * @param data
     * @return
     */
    IPage<MsgsCenterInfoPageResultDTO> page(IPage<MsgsCenterInfoPageResultDTO> page, MsgsCenterInfoQueryDTO data);

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
    boolean isChecked(Map<String,Object> params);


    int queryUnDeal();

    int queryUnRead();
}
