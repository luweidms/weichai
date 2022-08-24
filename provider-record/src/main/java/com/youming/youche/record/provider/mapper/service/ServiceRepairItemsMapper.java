package com.youming.youche.record.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.service.ServiceProduct;
import com.youming.youche.record.domain.service.ServiceRepairItems;
import com.youming.youche.record.domain.service.ServiceRepairItemsVer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022/1/8 16:40
 */
public interface ServiceRepairItemsMapper extends BaseMapper<ServiceRepairItems> {

    /**
     * @param stationId   加油站id(找油网标识)
     * @param productType
     * @return
     */
    ServiceProduct getServiceProductByStationId(@Param("stationId") String stationId,
                                                @Param("productType") Integer productType);

    /**
     * 查询 服务商维修项
     *
     * @param repairOrderId 维修保养订单id
     * @return
     */
    List<ServiceRepairItems> getRepairOrderItems(@Param("repairOrderId") long repairOrderId);

    /**
     * @param repairOrderId 维修保养订单id
     * @param repairHisId   维修保养主历史表id
     * @return
     */
    List<ServiceRepairItemsVer> getRepairOrderItemsVer(@Param("repairOrderId") long repairOrderId,
                                                       @Param("repairHisId") long repairHisId);

    void updataRepairItemsVer(@Param("id") Long id,@Param("pid") Long pid);
    /***
     * @Description: 根据id删除服务商维修项
     * @Author: luwei
     * @Date: 2022/4/8 2:22 下午
     * @Param id:
     * @return: void
     * @Version: 1.0
     **/
    void delRepairItems(@Param("id")Long id);

    /***
     * @Description: 根据id删除维修零配件
     * @Author: luwei
     * @Date: 2022/4/8 2:21 下午
     * @Param id:
     * @return: void
     * @Version: 1.0
     **/
    void delRepairParts(@Param("id")Long id);
}
