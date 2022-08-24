package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VehicleExpenseDetailedDto implements Serializable {


    private static final long serialVersionUID = -3919220167826431444L;

    /**
     * id
     */
    private Long vehicleExpenseDetailedId;
    /**
     * 状态：1待审核，2审核中，3待支付，4已完成
     */
    private Integer state;

    /**
     * 费用类型
     */
    private Long typeId;

    /**
     * 费用类型
     */
    private Long type;
    /**
     * 费用类型
     */
    private String typeName;

    /**
     * 申请人id
     */
    private Long applyId;

    /**
     * 申请人姓名
     */
    private String applyName;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 申请金额
     */
    private String applyAmount;

    /**
     * 描述
     */
    private String introduce;

    /**
     * 费用使用归属日期
     */
    private String expenseTime;

    /**
     * 附件
     */
    private String file;

    /**
     * 支付方式：1:油卡，2：现金
     */
    private Integer paymentType;
    /**
     * 油卡卡号
     */
    private String oilCardNumber;
    /**
     * 加油里程
     */
    private String refuelingMileage;

    /**
     * 加油升数
     */
    private String refuelingLiters;


}
