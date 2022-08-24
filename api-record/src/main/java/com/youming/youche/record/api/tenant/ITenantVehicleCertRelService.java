package com.youming.youche.record.api.tenant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRel;

import java.util.List;

/**
 * @Date:2021/12/27
 */
public interface ITenantVehicleCertRelService extends IService<TenantVehicleCertRel> {


    /**
     * 方法实现说明  根据关系表id查询证照信息
     *
     * @param relId
     * @return com.youming.youche.record.domain.tenant.TenantVehicleCertRel
     * @throws
     * @author terry
     * @date 2022/3/6 21:11
     */
    TenantVehicleCertRel selectByRelId(Long relId);

    /**
     * 查询车辆证照
     */
    List<TenantVehicleCertRel> findPlateNumber(String plateNumber);
}
