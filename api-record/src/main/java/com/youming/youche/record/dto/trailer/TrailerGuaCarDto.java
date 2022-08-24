package com.youming.youche.record.dto.trailer;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TrailerGuaCarDto implements Serializable {
    private Integer id;
    /**
     * 挂车车牌
     */
    private String  trailerNumber;

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
    /**
     * 资产净值总额
     */
    private Long zcjzze;
    /**
     * 剩余折旧期数总额
     */
    private Integer syzjqsze;

    /**
     * 资产单月折旧总额
     */
    private Long zcdyzjze;
    /**
     * 车辆数量
     */
    private Long count;

}
