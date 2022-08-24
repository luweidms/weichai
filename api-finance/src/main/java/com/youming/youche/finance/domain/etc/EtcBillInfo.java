package com.youming.youche.finance.domain.etc;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class EtcBillInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 账单开始日期
     */
    private LocalDateTime billStartDate;

    /**
     * 账单结束日期
     */
    private LocalDateTime billEndDate;

    /**
     * 服务商id
     */
    private Long serviceProviderId;

    /**
     * 服务商名称
     */
    private String serviceProviderName;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 卡类型
     */
    private Integer cardType;

    /**
     * 账期说明
     */
    private String accountPeriodRemark;

    /**
     * 最后还款日
     */
    private LocalDateTime finalPaymentDate;

    /**
     * 通行次数
     */
    private Integer trafficTimes;

    /**
     * 通行金额
     */
    private Long trafficFee;

    /**
     * 服务费
     */
    private Long serviceFee;

    /**
     * 滞纳金
     */
    private Long lateFee;

    /**
     * 应付金额
     */
    private Long shouldPayFee;

    /**
     * 已付通行费
     */
    private Long paidTrafficFee;

    /**
     * 已付服务费
     */
    private Long paidServiceFee;

    /**
     * 已付滞纳金
     */
    private Long paidLateFee;

    /**
     * 已付总额
     */
    private Long paidSumFee;

    /**
     * 未付通行费
     */
    private Long nopayTrafficFee;

    /**
     * 未付服务费
     */
    private Long nopayServiceFee;

    /**
     * 未付滞纳金
     */
    private Long nopayLateFee;

    /**
     * 未付金额
     */
    private Long nopaySumFee;

    /**
     * 来源类型
     */
    private Integer sourceType;

    /**
     * 状态  1, 未支付2, 已支付3, 部分支付4, 部分支付中5, 支付中6, 审核通过7, 审核不通过8, 待审核
     */
    private Integer status;

    /**
     * 账单更新时间
     */
    private LocalDateTime billUpdateDate;

    /**
     * 账单更新状态 1更新过
     */
    private Integer billUpdateState;

    /**
     * 审核状态 1通过 0不通过
     */
    private Integer auditState;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 渠道
     */
    private String channelType;

    /**
     * 更新操作员id
     */
    private Long updateOpId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 车队账号
     */
    private String tenantPhone;

    /**
     * 业务编码
     */
    private String busiCode;

    /**
     * 是否可以审核 1可以 0不可以
     */
    private Integer isAllowAudit;
}
