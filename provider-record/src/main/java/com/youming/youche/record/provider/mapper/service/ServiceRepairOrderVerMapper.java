package com.youming.youche.record.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.service.ServiceRepairOrderVer;
import org.apache.ibatis.annotations.Param;

/**
 * @date 2022/1/10 18:53
 */
public interface ServiceRepairOrderVerMapper extends BaseMapper<ServiceRepairOrderVer> {

    /**
     * 维修保养订单
     *
     * @param flowId
     * @return
     */
    ServiceRepairOrderVer getServiceRepairOrderVer(@Param("flowId") long flowId);

    void updateServiceRepairOrderVerByFlowId(@Param("ver") ServiceRepairOrderVer serviceRepairOrderVer);

    void insertServiceRepairOrderVer(@Param("ver") ServiceRepairOrderVer serviceRepairOrderVer);
}
