package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class QueryOaloneDto implements Serializable {
    private Long LId; //借支ID
    private Long amount; //金额
    private Integer loanSubject; //科目
    private String loanSubjectName; //科目名称
    private String oaLoanId; //借支序列id
    private Long orderId; //订单号
    private Long payedAmount; //核销金额
    private String plateNumber; //车牌号
    private Integer sts; //状态
    private Integer isNeedBill; //是否需要开票
    private String isNeedBillName; //是否需要开票名称
    private String stsName; //状态名称
    private Long toWriteOff; //是否注销
    private String userName; //用户名
    private Long userId; //用户ID
    private LocalDateTime appDate; //申请时间
    private String appReason; //申请描述
}
