package com.youming.youche.market.domain.etc;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 * etc账单VER表
 * 聂杰伟
 *
 * @author Terry
 * @since 2022-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class EtcBillInfoVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 审核状态 1通过 0不通过
     */
    private Integer auditState;
    /**
     * 账单结束日期
     */
    private LocalDateTime billEndDate;
    /**
     * 账单编号
     */
    private String billNo;
    /**
     * 账单开始日期
     */
    private LocalDateTime billStartDate;
    /**
     * 账单更新时间
     */
    private LocalDateTime billUpdateDate;
    /**
     * 账单更新状态 1更新过
     */
    private Integer billUpdateState;
    /**
     * 业务编码
     */
    private String busiCode;
    /**
     * 卡类型
     */
    private Integer cardType;
    /**
     * 渠道
     */
    private String channelType;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 最后还款日
     */
    private LocalDateTime finalPaymentDate;
    /**
     * 滞纳金
     */
    private Long lateFee;
    /**
     * 未付滞纳金
     */
    private Long nopayLateFee;
    /**
     * 未付服务费
     */
    private Long nopayServiceFee;
    /**
     * 未付金额
     */
    private Long nopaySumFee;
    /**
     * 未付通行费
     */
    private Long nopayTrafficFee;
    /**
     * 操作员Id
     */
    private Long opId;
    /**
     * 已付滞纳金
     */
    private Long paidLateFee;
    /**
     * 已付服务费
     */
    private Long paidServiceFee;
    /**
     * 已付总额
     */
    private Long paidSumFee;
    /**
     * 已付通行费
     */
    private Long paidTrafficFee;
    /**
     * 账期（天）
     */
    private Integer paymentDays;
    /**
     * 产品Id
     */
    private Long productId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 流水号
     */
    private Long relSeq;
    /**
     * 服务费
     */
    private Long serviceFee;
    /**
     * 服务商Id
     */
    private Long serviceProviderId;
    /**
     * 服务商名称
     */
    private String serviceProviderName;
    /**
     * 应付金额
     */
    private Long shouldPayFee;
    /**
     * 来源类型
     */
    private Integer sourceType;

    /**
     * 状态
     */
    private Integer state;
    /**
     * 状态  1, 未支付2, 已支付3, 部分支付4, 部分支付中5, 支付中6, 审核通过7, 审核不通过8, 待审核
     */
    private Integer status;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 车队名称
     */
    private String tenantName;
    /**
     * 车队账号
     */
    private String tenantPhone;
    /**
     * 通行金额
     */
    private Long trafficFee;
    /**
     * 通行次数
     */
    private Integer trafficTimes;
    /**
     * 更新时间
     */
    private LocalDateTime updateDate;
    /**
     * 更新操作员ID
     */
    private Long updateOpId;


}
