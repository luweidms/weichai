package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/14
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class BillRecordsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 业务单号
    private String repairCode;

    // 收款方
    private String businessName;

    // 手机号码
    private String userBill;

    // 车牌号码
    private String plateNumber;

    // 主驾司机
    private String userName;

    //业务类型
    private Integer serviceProviderType;

    // 销售金额
    private String totalFee;

    // 交易时间
    private String deliveryDate;

    //结算金额
    private Long realityBillAmout;

}
