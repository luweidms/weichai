package com.youming.youche.finance.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-06-27
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AccountStatementOrderInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
    * 对账单ID
    */
    private Long accountStatementId;

    /**
    * 订单号
    */
    private Long orderId;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 线路名称
     */
    private String sourceName;

    /**
     * 司机ID
     */
    private Long carDriverId;

    /**
     * 司机名称
     */
    private String carDriverMan;

    /**
     * 司机手机号码
     */
    private String carDriverPhone;

    /**
     * 收款人
     */
    private Long payeeUserId;

    /**
     * 收款人名称
     */
    private String payee;

    /**
     * 收款人手机
     */
    private String payeePhone;

    /**
     * 是否代收
     */
    private Integer isCollection;

    /**
     * 中标价
     */
    private Long totalFee;

    /**
     * 预付现金
     */
    private Long preCashFee;

    /**
     * 总邮费
     */
    private Long totalOilFee;

    /**
     * 预付实体油卡
     */
    private Long preOilFee;

    /**
     * 预付虚拟油卡
     */
    private Long preOilVirtualFee;

    /**
     * 预付ETC
     */
    private Long preEtcFee;

    /**
     * 到付款
     */
    private Long arrivePaymentFee;

    /**
     * 尾款
     */
    private Long finalFee;

    /**
     * 到期状态
     */
    private Integer finalSts;

    /**
     * 已付尾款
     */
    private Long paidFinal;

    /**
     * 异常补偿
     */
    private Long exceptionIn;

    /**
     * 异常扣减
     */
    private Long exceptionOut;

    /**
     * 时效罚款
     */
    private Long finePrice;

    /**
     * 总运费
     */
    private Long totalAmount;

    /**
     * 已付
     */
    private Long paidAmount;

    /**
     * 未付
     */
    private Long noPaidAmount;


}
