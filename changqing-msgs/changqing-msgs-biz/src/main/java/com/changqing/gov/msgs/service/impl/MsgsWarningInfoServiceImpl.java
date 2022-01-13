package com.changqing.gov.msgs.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.msgs.dao.MsgsWarningInfoMapper;
import com.changqing.gov.msgs.dto.MsgsWarningInfoDTO;
import com.changqing.gov.msgs.entity.MsgsWarningInfo;
import com.changqing.gov.msgs.service.MsgsWarningInfoService;
import com.changqing.gov.msgs.service.RequestFeignManagerApiForEsap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author ：Angular
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.service.impl
 * @ClassName: MsgsWarningInfoServiceImpl
 * @date ：Created in 2021/1/8 14:25
 * @description：待处理告警业务逻辑接口实现
 * @modified By：
 * @version: v1.0.0$
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class MsgsWarningInfoServiceImpl extends SuperServiceImpl<MsgsWarningInfoMapper, MsgsWarningInfo> implements MsgsWarningInfoService {

    @Autowired
    RequestFeignManagerApiForEsap requestFeignManagerApiForEsap;

    /**
     * 分页查询待处理告警业务逻辑
     *
     * @param page
     * @param data
     * @return
     */
    @Override
    public IPage<MsgsWarningInfo> queryResult(IPage<MsgsWarningInfo> page, MsgsWarningInfoDTO data) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!"".equals(data.getMonitorTime()) && data.getMonitorTime() != null) {
            data.setMonitorTimeStart(LocalDateTime.parse(dtf.format(data.getMonitorTime()), dtf));
            data.setMonitorTimeEnd(LocalDateTime.parse(dtf.format(data.getMonitorTime()), dtf).plusDays(1));
        }
        if (!"".equals(data.getUpdateTime()) && data.getUpdateTime() != null) {
            data.setUpdateTimeStart(LocalDateTime.parse(dtf.format(data.getUpdateTime()), dtf));
            data.setUpdateTimeEnd(LocalDateTime.parse(dtf.format(data.getUpdateTime()), dtf).plusDays(1));
        }
        IPage<MsgsWarningInfo> pageMsgs = this.baseMapper.queryResult(page, data);


        Map<String, String> managerInfo = requestFeignManagerApiForEsap.managerUserList();
        List<MsgsWarningInfo> records = pageMsgs.getRecords();
        if (records != null && records.size() > 0) {
            for (MsgsWarningInfo ers : records) {
                ers.setReceiverUserName(managerInfo.get(String.valueOf(ers.getReceivier())));
                ers.setUpdateUserName(managerInfo.get(ers.getUpdateUser()));
                if (ers.getWarningLevel() != null) {
                    switch (ers.getWarningLevel()) {
                        case "1":
                            ers.setWarningLevel("一级");
                            break;
                        case "2":
                            ers.setWarningLevel("二级");
                            break;
                        case "3":
                            ers.setWarningLevel("三级");
                            break;
                        case "4":
                            ers.setWarningLevel("四级");
                            break;
                        case "5":
                            ers.setWarningLevel("五级");
                            break;
                        default:
                            ers.setWarningLevel("");
                    }
                }
            }

        }
        return pageMsgs;
    }

    @Override
    public String selectWarningCode(String warningCode,String receivier) {
        return this.baseMapper.selectWarningCode(warningCode,receivier);
    }

    @Override
    public boolean isDeal(String warningCode, String receivier, String isDeal, String updateUser, String updateTime) {
        int index = this.baseMapper.isDeal(warningCode, receivier, isDeal, updateUser, updateTime);
        return index > 0 ? true : false;
    }

    @Override
    public int insertMsgContent(String warningCode, String warningContent, String warningTime) {
        return this.baseMapper.insertMsgContent(warningCode, warningContent, warningTime);
    }

}
