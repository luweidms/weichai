package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class CooperationProductVo implements Serializable {

    private String priceType;
    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 租户联系人
     */
    private String linkMan;
    /**
     * 联系电话
     */
    private String linkPhone;
    /**
     * 油价
     */
    private Long oilPrice;
    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;
    /**
     * 账期
     */
    private Integer paymentDays;

    /**
     * 结算方式，1账期，2月结
     */
    private Integer balanceType;

    private String balanceTypeName;

    /**
     * 账期结算月份
     */
    private Integer paymentMonth;

    /**
     * 授信金额
     */
    private Long quotaAmt;

    /**
     * 已使用授信金额
     */
    private Long useQuotaAmt;


    /**
     * 是否开票（1.是、2.否）
     */
    private String isBill;

    private String stateName;

    /**
     * '状态（1.有效、2.无效）',
     */
    private Integer state;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 站点ID
     */
    private Long productId;

    private List<logs> logs;
}
