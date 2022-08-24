package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 订单异常登记表
 *
 * @author hzx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderProblemInfo extends BaseDomain {

    /**
     * 异常ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 异常原因
     */
    private String problemReason;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 异常类型
     */
    private String problemType;

    /**
     * 图片ID，支持多图片
     */
    private String imgIds;

    /**
     * 多图片地址
     */
    private String imgUrls;

    /**
     * 创建时间
     */
//    private LocalDateTime createDate;

    /**
     * 异常状态 1、待处理 2、已处理 3、审核通过 4、审核不通过 8、撤销登记
     */
    private Integer state;

    /**
     * 记录用户ID
     */
    private Long recordUserId;

    /**
     * 异常登记金额
     */
    private Long problemPrice;

    /**
     * 异常处理后金额
     */
    private Long problemDealPrice;

    /**
     * 1、车主责任 2、货主责任
     */
    private Integer chargeType;

    /**
     * 车主ID
     */
    private Long carOwnerId;

    /**
     * 车主姓名
     */
    private String carOwnerName;

    /**
     * 车主号码
     */
    private String carOwnerPhone;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 审核备注
     */
    private String verifyDesc;

    /**
     * 异常大类型 1成本异常；2收入异常
     */
    private Integer problemCondition;

    /**
     * 报表提取标识0未提取1已提取
     */
    private Integer reportSts;

    /**
     * 提取日期
     */
    private LocalDateTime reportDate;

    /**
     * 申诉原因
     */
    private String appealReason;

    /**
     * 异常来源
     */
    private Integer sourceProblem;

    /**
     * 外部异常状态
     */
    private Integer problemExternalState;

    /**
     * 来源异常ID
     */
    private Long problemSourceId;

    /**
     * 责任方名称
     */
    private String responaiblePartyName;

    /**
     * 审核完成时间
     */
    private LocalDateTime verifyDate;

    /**
     * 抵扣金额
     */
    private Long deductionFee;

    /**
     * 到付抵扣金额
     */
    private Long arriveDeductionFee;

    /**
     * 尾款抵扣金额
     */
    private Long finalDeductionFee;

}
