package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("tenant_vehicle_zc")
public class VehicleDateCostRel extends BaseDomain {
    private static final long serialVersionUID = 1L;
    /**
     * 牌照类型(1:整车，2：拖头)   /挂车
     */
    private Integer licenceType;
    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 挂车车牌
     */
    private String trailerNumber;
    /**
     * 车辆品牌
     */
    private String brandModel;
    /**
     * 资产净值
     */
    private Long zcjz;
    /**
     * 剩余折旧期数
     */
    private Integer syzjqs;

    /**
     * 资产单月折旧
     */
    private Long zcdyzj;

    /**
     * 车辆状态
     */
    private String states;
    /**
     * 车辆性质
     */
    private String xingzhi;




}
