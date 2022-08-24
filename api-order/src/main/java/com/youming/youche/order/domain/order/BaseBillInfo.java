package com.youming.youche.order.domain.order;

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
 * @author liangyan
 * @since 2022-03-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BaseBillInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 附加运费
     */
    private Long appendFreight;

    /**
     * 申请号
     */
    private String applyNum;

    /**
     * 票据成本
     */
    private Long billCost;

    /**
     * 总服务费
     */
    private Long billServiceAmount;

    /**
     * 开票状态：1未开票>2已开票>3开票完成
     */
    private Integer billState;

    /**
     * 票据类型
     */
    private Integer billType;

    /**
     * 现金
     */
    private Long cash;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * etc
     */
    private Long etc;

    /**
     * 订单ETC利润
     */
    private Long income;

    /**
     * 是否开票
     */
    private Integer isNeedBill;

    /**
     * 是否进入报表,NULl初始化,1成功进入报表,2失败
     */
    private Integer isReport;

    /**
     * 未提现金额
     */
    private Long noWithdrawAmount;

    /**
     * 油金额
     */
    private Long oil;

    /**
     * 油的资金渠道
     */
    private String oilAffiliation;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 开票方
     */
    private String openBillUser;

    private Integer orderFund;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 付款账户编号
     */
    private String payAcctName;

    /**
     * 付款方id
     */
    private Long payUserId;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 总的平台提成
     */
    private Long platformRoyalty;

    /**
     * 收款账户名称
     */
    private String receiveAcctName;

    /**
     * 收款方
     */
    private Long receiveUserId;

    /**
     * 处理描述
     */
    private String reportRemark;

    /**
     * 处理时间
     */
    private LocalDateTime reportTime;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 修改人
     */
    private Long updateOpId;

    /**
     * 資金渠道
     */
    private String vehicleAffiliation;

    /**
     * 车辆类别
     */
    private Integer vehicleClass;

    /**
     * 提现金额
     */
    private Long withdrawAmount;


}
