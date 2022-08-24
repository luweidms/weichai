package com.youming.youche.record.domain.base;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 中交北斗缴费记录
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BeidouPaymentRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 业务单号
     */
    private String busiCode;

    /**
     * 缴费金额（单位分）
     */
    private Long amount;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 订单靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * 车牌编号
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 司机Id
     */
    private Long carDriverId;

    /**
     * 司机姓名
     */
    private String carDriverName;

    /**
     * 司机手机
     */
    private String carDriverPhone;

    /**
     * 缴费时间
     */
    private LocalDateTime payTime;

    /**
     * 支付状态 1已支付 0未支付
     */
    private Integer payState;

    /**
     * 生效时间
     */
    private LocalDateTime effectDate;

    /**
     * 失效时间
     */
    private LocalDateTime invalidDate;

    /**
     * 记录状态 1有效 0无效
     */
    private Integer state;

    /**
     * 渠道：1车辆发起缴费，2单发起的缴费
     */
    private String channelType;

    /**
     * 缴费方式：1按天，2按月，3按年
     */
    private Integer payType;

    /**
     * 缴费方式费用：如按天缴费10元/天，则1000分
     */
    private Long payTypeAmount;

    /**
     * 批次号
     */
    private Long soNbr;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 车队账户
     */
    private String tenantBill;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    private LocalDateTime updateDate;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 处理状态null初始化，1成功，2失败
     */
    private Integer dealState;

    /**
     * 处理描述
     */
    private String dealRemark;

    /**
     * 交易时间
     */
    private LocalDateTime dealTime;

    /**
     * 付款方类型
     */
    private Integer payUserType;

    /**
     * 收款方类型
     */
    private Integer receUserType;


}
