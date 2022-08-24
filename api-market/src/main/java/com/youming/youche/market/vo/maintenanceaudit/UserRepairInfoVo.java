package com.youming.youche.market.vo.maintenanceaudit;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRepairInfoVo implements Serializable {
    /**
     * 单号
     */
    private String repairCode;
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 服务商名
     */
    private String businessName;
    /**
     * 商品名
     */
    private String oilNum;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 消费人名
     */
    private String userName;
    /**
     * 消费人号码
     */
    private String userBill;
    /**
     *维修时间
     */
    private String repairDate;
    /**
     * 交付时间
     */
    private String deliveryDate;
    /**
     * 金额
     */
    private Double totalFee;
    /**
     * 商品开票折扣率
     */
    private String oilRateInvoice;
    /**
     * 维修单状态
     */
    private Integer repairState;
    /**
     * 主键
     */
    private Long repairId;

    private String repairStateName;

    private Integer hasVer;
}
