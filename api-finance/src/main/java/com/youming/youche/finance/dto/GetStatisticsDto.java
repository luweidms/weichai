package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetStatisticsDto implements Serializable {

    private static final long serialVersionUID = -5627654349999051446L;

    private Integer oaLoanCout; // 借支审核

    private Integer claimInfoCount; // 报销审核

    private Long applyCount; // 邀请审核

    private Integer orderTrackingCount; // 订单跟踪

    private Integer onlineReceiptCount; //在线接单

    private Integer orderAuditCount; // 订单审核

    private Long costReportAuditCount; // 费用审核

    private Integer payManagerCount;

    private Integer carCostReportAuditCount; // 车辆费用审核

}
