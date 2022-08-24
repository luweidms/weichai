package com.youming.youche.market.api.repair;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.repair.RepairItems;
import com.youming.youche.market.dto.user.RepairItemsDto;

import java.util.List;
import java.util.Map;

/**
 * 维修项
 *
 * @author hzx
 * @date 2022/3/14 10:15
 */
public interface IRepairItemsService extends IBaseService<RepairItems> {

    /**
     * 获取维修项
     *
     * @Param repairId 维修单id
     */
    List<RepairItemsDto> getRepairItems(Long repairId);


    /**
     * 查询维修项
     * @param tenantId
     * @param repairId
     * @return
     */
    List<RepairItems> getRepairItemsList(Long tenantId, Long repairId);

    /**
     * 删除维修项
     * @param repairItems
     */
    void delRepairItems(RepairItems repairItems);

    /**
     * 接口编码：40009
     * 获取一级维修项
     * @return
     */
    List<Map<String,Object>> getLevelOneRepair();

    /**
     * 接口编码：40010
     * 获取子维修项
     * @return
     */
    List<Map<String,Object>> getLevelTwoRepair();

}
