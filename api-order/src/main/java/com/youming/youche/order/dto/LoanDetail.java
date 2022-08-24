package com.youming.youche.order.dto;

import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import lombok.Data;

import java.io.Serializable;
@Data
public class LoanDetail  implements Serializable {
    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 借支序列id
     */
    private String oaLoanId;

    /**
     * 科目
     */
    @SysStaticDataInfoDict(dictDataSource= "LOAN_SUBJECT")
    private Integer loanSubject;

    /**
     * 金额(单位分)
     */
    private Long amount;



    /**
     * 核销金额（分）
     */
    private Long payedAmount;

    /**
     * 未核销金额（分）
     */
    private String noPayAmount;

    @SysStaticDataInfoDict(dictDataSource= "LOAN_STATE")
    private String sts;

    /**
     * 申请时间
     */
    private String appDate;


}
