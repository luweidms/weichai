package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GetVehicleExpenseDto implements Serializable {

    private static final long serialVersionUID = -7390778369105900409L;

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 申请开始时间
     */
    private String beginApplyTime;

    /**
     * 申请结束时间
     */
    private String endApplyTime;

    /**
     * 费用类型 1维修，2保养，3加油，4杂费
     */
    private Integer type;

    /**
     * 状态 1待审核，2审核中，3待支付，4已完成
     */
    private Integer state;

    /**
     * 费用归属
     */
    private String expenseDepartment;

    /**
     * 费用归属id
     */
    private Long orgId;

    /**
     * 申请金额 起始
     */
    private Long beginApplyAmount;

    /**
     * 申请金额 终止
     */
    private Long endApplyAmount;

    /**
     * 申请人
     */
    private String applyName;
    private Integer pageNum=1;
    private Integer pageSize=10;

    private Long tenantId;
    private LocalDateTime beginApplyTime1;
    private LocalDateTime endApplyTime1;

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     *待我处理状态
     */
    private Boolean waitDeal;

    /**
     * 费用类型 1维修，2保养，3加油，4杂费
     */
    private Integer typeId;


}
