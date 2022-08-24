package com.youming.youche.record.api.vehicle;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;
import com.youming.youche.record.dto.tenant.TenantVehicleRelQueryDto;
import com.youming.youche.record.vo.tenant.TenantVehicleRelQueryVo;

import java.util.List;

/**
 * <p>
 * 车队与车辆关系版本表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface ITenantVehicleRelVerService extends IService<TenantVehicleRelVer> {

    /**
     * 车辆编号查询车队与车辆历史表
     * @param vehicleCode 车辆编号
     * @return
     */
    TenantVehicleRelVer getTenantVehicleRelVer(Long vehicleCode);

    /**
     * 车辆编号查询车队与车辆历史表
     * @param vehicleCode 车辆编号
     * @param tenantId 车队id
     * @return
     */
    List<TenantVehicleRelVer> getVehicleObjectVer(Long vehicleCode, Long tenantId);

    /**
     * 查询车辆关系表中的数据
     */
    List<TenantVehicleRelQueryDto> doQueryVehicleSimpleInfoNoPage(TenantVehicleRelQueryVo tenantVehicleRelQueryVo);

    /**
     * 获取接收人信息
     */
    List<TenantVehicleRelQueryDto> doQueryBillReceiverNoPage(TenantVehicleRelQueryVo tenantVehicleRelQueryVo);

    /**
     * 查询历史车辆车队关系信息
     *
     * @param id        车牌编号
     * @param tenantId  车队id
     * @param beginDate 创建时间开始
     * @param endDate   创建时间结束
     */
    List<TenantVehicleRelVer> lists(Long id, Long tenantId, String beginDate, String endDate);

}
