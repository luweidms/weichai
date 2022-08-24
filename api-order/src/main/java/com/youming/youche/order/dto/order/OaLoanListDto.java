package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OaLoanListDto implements Serializable {

    private static final long serialVersionUID = 5976893784848477387L;

    private Long LId; // 借支id
    private Long amount; // 金额(单位分)
    private Integer loanSubject; // 科目
    private String loanSubjectName;
    private Integer isNeedBill; // 是否需要开票 0无需 1需要
    private String isNeedBillName;//票据类型
    private String oaLoanId; // 借支序列id
    private Long orderId; // 订单编号
    private String plateNumber; // 车牌号
    private String phoneNumber;
    private String userName;
    private Integer sts;
    private String stsName;
    private Boolean hasPermission;

}
