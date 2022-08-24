package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: BeApplyRecordVehicleVo
 * @Package: com.youming.youche.record.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/25 14:06
 * @company:
 */

@Data
public class BeApplyRecordVehicleVo implements Serializable {
    private String plateNumber;

    private String vehicleLength;

    private String vehicleStatus;

    private String vehicleStatusName;

    private String vehicleTypeString;

    private Boolean checked;

    private String billReceiverName;

    private String billReceiverMobile;
}
