package com.youming.youche.market.domain.etc;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * ETC 管理表
 * </p>
 *
 * @author 聂杰伟
 * @since 2022-03-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmEtcInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 对账单编码
     */
    private String accountStatementNo;

    /**
     * 预支手续费(单位分)
     */
    private Long advanceFee;

    /**
     * 欠款扣除金额(单位分)
     */
    private Long arrearsFee;

    /**
     * 扣费前的用户预支金额(单位分)
     */
    private Long beforePay;

    /**
     * 账单结束时间
     */
    private LocalDateTime billEndDate;

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 账单开始时间
     */
    private LocalDateTime billStartDate;

    /**
     * 卡类型粤通卡、鲁通卡、浙通卡...
     */
    private Integer cardType;

    /**
     * 扣费状态：0未扣费,1已扣费,2扣费失败
     */
    private Integer chargingState;

    /**
     * 接收人手机
     */
    private String collectMobile;

    /**
     * 接收人
     */
    private String collectName;

    /**
     * 接收人Id
     */
    private Long collectUserId;

    /**
     * 消费折后金额
     */
    private Long consumeAfterMoney;

    /**
     * 消费金额
     */
    private Long consumeMoney;

    /**
     * 利润
     */
    private Long consumeProfit;

    /**
     * 消费机构
     */
    private String consumeStruct;

    /**
     * 扣取车主费用日期
     */
    private LocalDateTime cutPaymentDay;

    /**
     * 是否扣取成功 0未成功；1成功
     */
    private Integer cutPaymentState;

    /**
     * 出站名
     */
    private String departureName;

    /**
     * 出站时间
     */
    private LocalDateTime departureTime;

    /**
     * 扣费前的车辆etc金额(单位分)
     */
    private Long etcAmount;

    /**
     * etc账户扣除金额(单位分)
     */
    private Long etcAmountDeduct;

    /**
     * 卡号
     */
    private String etcCardNo;

    /**
     * 消费时间
     */
    private LocalDateTime etcConsumeTime;

    /**
     * 导入ETC用户类型：2招商车、3自有车
     */
    private Integer etcUserType;

    /**
     * 导入时间
     */
    private LocalDateTime importTime;

    /**
     * 入站名
     */
    private String inboundName;

    /**
     * 入站时间
     */
    private LocalDateTime inboundTime;

    /**
     * 判断是否覆盖 0：不覆盖 1：覆盖
     */
    private Integer isCover;

    /**
     * 扣费前的用户未到期金额(单位分)
     */
    private Long marginBalance;

    /**
     * 即将到期余额扣除金额(单位分)
     */
    private Long marginBalanceDeduct;

    /**
     * 操作时间
     */
    private LocalDateTime opDate;

    /**
     * 操作人ID
     */
    private Long opId;

    /**
     * 自有车回写的订单ID
     */
    private Long orderId;

    /**
     * 创建网点ID
     */
    private Long orgId;

    /**
     * 付费类型
     */
    private Integer paymentType;

    /**
     * 成本模式（跟订单结算模式同步）
     */
    private Integer paymentWay;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 站点编号
     */
    private Long productId;

    /**
     * 站点名称
     */
    private String productName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 服务商Id
     */
    private Long serviceProviderId;

    /**
     * 服务商名称
     */
    private String serviceProviderName;

    /**
     * 与ETC供应商结算月份
     */
    private String settlementMonth;

    /**
     * 来源类型（导入、平台下发）
     */
    private Integer sourceType;

    /**
     * 数据有效状态0：无效，1有效
     */
    private Integer state;

    /**
     * 创建租户ID
     */
    private Long tenantId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 车队账户
     */
    private String tenantPhone;

    /**
     * 交易摘要
     */
    private String tradingPaper;

    /**
     * 交易地点
     */
    private String tradingSite;

    /**
     * 用户ID/发布人用户编号(司机归属用户ID)
     */
    private Long userId;

    /**
     * 车辆摘要
     */
    private String vehiclePaper;

    /**
     * 是否核销 0未核销；1已核销
     */
    private Integer verification;

    /**
     * 可提现余额扣除金额(单位分)
     */
    private Long withdrawalAmountDeduct;

    // 判断是否覆盖名称
    @TableField(exist = false)
     private  String isCoverName;
    @TableField(exist = false)
    private  String ChargingStateName; ////扣费状态名称
    @TableField(exist = false)
    private  String paymentWayName;   //成本模式

    @TableField(exist = false)
    private  String cardTypeName;//卡类型名称 1鲁通卡 2粤通卡 3浙通卡...

    @TableField(exist = false)
    private  String paymentTypeName; //付款类型名称 1预付费 2后付费

    /**
     * 来源类型（导入、平台下发）
     */
    @TableField(exist = false)
    private  String sourceTypeName;

    /**
     * 消费时间
     */
    @TableField(exist = false)
    private Date ConsumeTime;
    @TableField(exist = false)
    private String etcUserTypeName; //车辆类型
}
