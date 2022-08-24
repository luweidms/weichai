package com.youming.youche.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author WuHao
* @since 2022-04-13
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ZhangPingOrderRecord extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long flowId;

    private Integer accountOrderType;

    private Long batchId;

    private LocalDateTime createDate;

    private Long faceBalanceUnused;

    private Long faceBalanceUnusedAfter;

    private Long faceMarginUnused;

    private Long faceMarginUnusedAfter;

    private Integer isDriverOrder;

    private Integer isReport;

    private Long noPayCash;

    private Long noPayCashAfter;

    private Long noPayDeb;

    private Long noPayDebAfter;

    private Long noPayEtc;

    private Long noPayEtcAfter;

    private Long noPayFinal;

    private Long noPayFinalAfter;

    private Long noPayOil;

    private Long noPayOilAfter;

    private Long noWithdrawCash;

    private Long noWithdrawCashAfter;

    private Long orderId;

    private String remark;

    private LocalDateTime reportDate;

    private String reportRemark;

    private Long tenantId;

    private Long userId;

    private Integer userType;

    private String vehicleAffiliation;

    private Long zhangPingCash;

    private Long zhangPingDeb;

    private Long zhangPingEtc;

    private Long zhangPingFaceBalance;

    private Long zhangPingFaceMargin;

    private Long zhangPingFinal;

    private Long zhangPingOil;

    private Integer zhangPingOrderType;

    private Long zhangPingWithdrawCash;


}
