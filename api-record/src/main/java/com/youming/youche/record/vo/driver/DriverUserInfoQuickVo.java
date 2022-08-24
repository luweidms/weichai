package com.youming.youche.record.vo.driver;

import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @version:
 * @Title: DriverUserInfoQuickVo
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/26 15:26
 * @company:
 */
@Data
public class DriverUserInfoQuickVo implements Serializable {
    //司机基本信息
    private Boolean driverFlag;
    private Long userId;
    private String linkman;
    private Boolean ownFlag;
    private Long tenantId;
    private String tenantName;

    //车辆基本信息
    private List<VehicleDataInfo> vehicleDataInfoList;
}
