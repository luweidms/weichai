package com.youming.youche.record.dto.trailer;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
public class VehicleDateCostRelDto implements Serializable {

    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 车辆品牌
     */
    private String brandModel;
    /**
     * 购买时间
     */
    private Date purchaseDate;

    /**
     * 折旧月数
     */
    private Integer depreciatedMonth;

    /**
     * 价格
     */
    private Long price;
    /**
     * 残值
     */
    private Long residual;
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







}
