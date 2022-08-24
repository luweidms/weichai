package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 服务商etc表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceProductEtc extends BaseDomain {

    private static final long serialVersionUID = 1L;
    /**
     * 账期
     */
    private String accountPeriodRemark;
    /**
     * 优惠说明
     */
    private String discountRemark;
    /**
     * ETC卡类型 1粤通卡 2鲁通卡 3赣通卡
     */
    private Integer etcCardType;
    /**
     * 滞纳金
     */
    private String lateFeeRemark;
    /**
     * 收款方信息（正常还款）',
     */
    private Long nrAccId;
    /**
     * 收款方信息（正常还款）服务商
     */
    private Long nrServiceUserId;
    /**
     * 收款方信息（超期还款）
     */
    private Long orAccId;
    /**
     * 收款方信息（超期还款）服务商
     */
    private Long orServiceUserId;
    /**
     * 付费类型 1预付费 2后付费
     */
    private Integer paymentType;
    /**
     * 平台开卡费
     */
    private String platformCardFeeRemark;
    /**
     * 平台服务费
     */
    private Integer pltSerFeeType;
    /**
     * 费率
     */
    private String rateRemark;
    /**
     * 还款日
     */
    private String repaymentDateRemark;
    /**
     * 服务商ID
     */
    private Long serviceUserId;
    /**
     * 账单日
     */
    private String statementDateRemark;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 使用范围 1企业 2个人
     */
    private Integer useScope;


}
