package com.changqing.gov.msgs.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.msgs.dao.MsgsCenterInfoReceiveMapper;
import com.changqing.gov.msgs.entity.MsgsCenterInfoReceive;
import com.changqing.gov.msgs.service.MsgsCenterInfoReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 消息中心 接收表
 * 全量数据
 * </p>
 *
 * @author changqing
 * @date 2019-08-01
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class MsgsCenterInfoReceiveServiceImpl extends SuperServiceImpl<MsgsCenterInfoReceiveMapper, MsgsCenterInfoReceive> implements MsgsCenterInfoReceiveService {

}
