package com.youming.youche.order.dto;

import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import lombok.Data;

import java.io.Serializable;


@Data
public class PeccancDetailDto implements Serializable {
    /**
     * 报销人ID
     */
    private Long borrowUserId;


    /**
     * 借支序列id
     */
    private String oaLoanId;

    /**
     * 审核完成时间
     */
    private String verifyDate;

    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 服务费金额
     */
    private Long amount;


    private Long mainAmount;

    @SysStaticDataInfoDict(dictDataSource = "LOAN_SUBJECT")
    private Integer subjectId;


    private Long copilotAmount;

}
