package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class VehicleCountDto implements Serializable {

    private static final long serialVersionUID = 8431104899748475489L;

    private Integer authState; // 深恶黑状态

    private String authStateName; // 审核状态名称

    private Long vehicleCount; // 车辆数

    private Map rtnMap; // 车辆信息
}
