package com.youming.youche.order.vo.monitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 运营大屏
 *
 * @author hzx
 * @date 2022/3/9 11:05
 */
@Data
public class MonitorOrderAgingVo implements Serializable {

    private static final long serialVersionUID = -1L;

    // 是否是车队
    private Boolean isTenant;

    // 业务主键 tenandId 或 orderId
    private Long busiId;

    //线路-出发城市
    private Integer sourceRegion;

    //线路-目的城市
    private Integer desRegion;

    // 组织部门 / 部门编号
    private Long orgId;

    // 车牌号码
    private String plateNumber;

    //事件编码
    private String eventCode;

    //车类型
    private Integer vehicleClass;

    private Integer agingType;

    public boolean isOtherVehicleClass() {
        return vehicleClass != null && (vehicleClass == 3 || vehicleClass == 2 || vehicleClass == 4);
    }

    public boolean isSelfVehicleClass() {
        return vehicleClass != null && vehicleClass == 1;
    }

}
