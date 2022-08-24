package com.youming.youche.record.provider.mapper.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.dto.service.ServiceRepairOrderDto;
import com.youming.youche.record.vo.service.ServiceRepairOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022/1/8 10:59
 */
public interface ServiceRepairOrderMapper extends BaseMapper<ServiceRepairOrder> {

    Page<ServiceRepairOrderDto> doQueryOrderList(@Param("serviceRepairOrderVo") ServiceRepairOrderVo serviceRepairOrderVo,
                                                 Page<ServiceRepairOrderDto> objectPage);

    List<ServiceRepairOrderDto> doQueryOrderListExport(@Param("serviceRepairOrderVo") ServiceRepairOrderVo serviceRepairOrderVo);

    Double getTotalFee(@Param("inParam") ServiceRepairOrderVo serviceRepairOrderVo);

    ServiceRepairOrder selectRecordById(@Param("flowId") long flowId);

    void updateOrderStatusById(@Param("serviceRepairOrder") ServiceRepairOrder serviceRepairOrder);

    void insertServiceRepairOrder(@Param("serviceRepairOrder") ServiceRepairOrder serviceRepairOrder);

    void updateServiceRepairOrderById(@Param("entity") ServiceRepairOrder serviceRepairOrder);

    int updateLastOrderMileageById(@Param("serviceRepairOrder") ServiceRepairOrder serviceRepairOrder);

    /**
     * 营运工作台  维保费用  待我审
     */
    List<WorkbenchDto> getTableMaintenanceCount();

    /**
     * 营运工作台  维保费用  我发起
     */
    List<WorkbenchDto> getTableMaintenanceMeCount();
}
