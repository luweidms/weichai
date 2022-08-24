package com.youming.youche.record.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.service.ServiceRepairPartsVer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022/1/21 18:49
 */
public interface ServiceRepairPartsVerMapper extends BaseMapper<ServiceRepairPartsVer> {

    /**
     * @param repairOrderId 维修保养订单id
     * @param repairHisId   维修保养主历史表id
     * @param orderItemId   项目id
     * @return
     */
    List<ServiceRepairPartsVer> getRepairOrderPartsVer(@Param("repairOrderId") Long repairOrderId,
                                                       @Param("repairHisId") Long repairHisId,
                                                       @Param("orderItemId") Long orderItemId);

    void updateRepairPartsVer(@Param("id") Long id,@Param("pid") Long pid);

}
