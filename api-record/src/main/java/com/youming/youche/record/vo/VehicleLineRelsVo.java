package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/1/12
 */
@Data
public class VehicleLineRelsVo implements Serializable {

    private Long lineId;

    private String backhaulNumber;

    private Integer state;

    private String lineCodeRule;

    private Long relId;

    private Long vehicleCode;

    private String plateNumber;


}
