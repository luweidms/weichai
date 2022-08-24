package com.youming.youche.order.domain;

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
* @author wuhao
* @since 2022-04-25
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class TurnCashOrder extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流水号
     */
            //@TableId(value = "flow_id", type = IdType.AUTO)
    private Long flowId;
    /**
     * 加油使用现金金额(分)
     */
    private Long balance;
    /**
     * 批次号
     */
    private Long batchId;
    /**
     * 订单未付油
     */
    private Long canTurnBalance;
    /**
     * 司机类型
     */
    private Integer carUserType;
    /**
     * 消費订单金额
     */
    private Long consumeOrderBalance;
    /**
     * 可扣除的贷款油
     */
    private Long deductibleLoanOil;
    /**
     * 待抵扣欠款
     */
    private Long deductibleMargin;
    /**
     * 折扣金額
     */
    private Long discountAmount;
    /**
     * ETC金额
     */
    private Long etcBalance;
    /**
     * 利潤
     */
    private Long income;
    /**
     * 是否进入报表,NULl初始化,1成功进入报表,2失败
     */
    private Integer isReport;
    /**
     * 加油使用未到期金额(分)
     */
    private Long marginBalance;
    /**
     * 油账户明细 返回 值
     */
    private Long oilBalance;
    /**
     * 油卡卡号
     */
    private String oilCardNumber;
    /**
     * 订单etc金额
     */
    private Long orderEtcBalance;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 订单油金额
     */
    private Long orderOilBalance;
    /**
     * 备注
     */
    private String remark;
    /**
     * 提取日期
     */
    private LocalDateTime reportDate;
    /**
     * 处理描述
     */
    private String reportRemark;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 已转金额
     */
    private Long turnBalance;
    /**
     * 转移折扣
     */
    private Long turnDiscount;
    /**
     * 转现月份
     */
    private String turnMonth;
    /**
     * 转现类型(1油转现，2etc转现)
     */
    private Integer turnType;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用戶类型
     */
    private Integer userType;
    /**
     * 资金渠道
     */
    private String vehicleAffiliation;


}
