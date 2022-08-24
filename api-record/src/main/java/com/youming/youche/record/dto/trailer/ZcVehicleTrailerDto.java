package com.youming.youche.record.dto.trailer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ZcVehicleTrailerDto implements Serializable {
    private Long id;
    /**
     * 牌照类型(1:整车，2：拖头 3：挂车)
     */
    private Integer licenceType;
    /**
     * 资产净值总额
     */
    private String zcjzze;
    /**
     * 资产净值总额
     */
    private double zcjzzeL;
    /**
     * 剩余折旧期数总额
     */
    private Integer syzjqsze;

    /**
     * 资产单月折旧总额
     */
    private String zcdyzjze;
    /**
     * 资产单月折旧总额
     */
    private double zcdyzjzeL;
    /**
     * 车辆数量
     */
    private int count;
    /**
     * 对应子类 (整车 牵引车)
     */
    private List<DateCostDto> children;

}
