package com.youming.youche.record.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.service.ServiceRepairParts;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022/1/20 17:27
 */
public interface ServiceRepairPartsMapper extends BaseMapper<ServiceRepairParts> {

    /**
     * 查询 维修零配件
     *
     * @param repairOrderId 维修保养订单id
     * @return
     */
    List<ServiceRepairParts> getRepairOrderParts(@Param("repairOrderId") long repairOrderId);

}
