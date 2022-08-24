package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 附加运费
 * </p>
 *
 * @author hzx
 * @since 2022-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AdditionalFee extends BaseDomain {

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 资金渠道
     */
    private String vehicleAffiliation;

    /**
     * 司机用户id
     */
    private Long carUserId;

    /**
     * 司机姓名
     */
    private String carDriverMan;

    /**
     * 司机手机号
     */
    private String carDriverPhone;

    /**
     * 客户名称
     */
    private String customName;

    /**
     * 线路名称
     */
    private String sourceName;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 到达市
     */
    private Integer desRegion;

    /**
     * 需求车长
     */
    private String vehicleLengh;

    /**
     * 需求车辆类型
     */
    private Integer vehicleStatus;

    /**
     * 车辆id
     */
    private Long vehicleCode;

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * 附加费金额
     */
    private Long appendFreight;

    /**
     * 付款时间
     */
    private LocalDateTime payTime;

    /**
     * 返还时间
     */
    private LocalDateTime returnTime;

    /**
     * 支付流水号
     */
    private Long flowId;

    /**
     * 业务单号
     */
    private String busiCode;

    /**
     * 三方编码
     */
    private String thirdCode;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 状态：1车队待付款，2车队已付款，3开票平台已返还，4失效
     */
    private Integer state;

    /**
     * 收款银行卡账户名
     */
    private String receivablesBankAccName;

    /**
     * 收款银行卡账号
     */
    private String receivablesBankAccNo;

    /**
     * 进程处理状态：0初始化，1成功，2失败
     */
    private Integer dealState;

    /**
     * 处理描述
     */
    private String dealRemark;

    private LocalDateTime dealTime;


}
