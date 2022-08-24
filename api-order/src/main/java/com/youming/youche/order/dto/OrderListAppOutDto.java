package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/20
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderListAppOutDto implements Serializable {
    private static final long serialVersionUID = -6861080929135141971L;

    private Long toTenantId;
    /**
     * 订单编号
     */
    private Long orderId;
    private String tenantName;
    private LocalDateTime dependTime;
    private Integer sourceRegion;//始发市
    private Integer desRegion;//到达市
    private Integer orderState;//订单状态
    private Long preTotalFee;//预付总计金额
    private Long finalFee;//尾款金额
    private String plateNumber;//车牌号
    private Integer reciveType;//回单类型
    private Long totalFee;//中标价
    private Long estFee;//总成本
    private String nand;//起始纬度
    private String eand;//起始经度
    private String nandDes;//目的纬度
    private String eandDes;//目的经度
    private Integer reciveState;//回单状态
    private Long preOilVirtualFee;//预付虚拟油
    private Long preOilFee;//预付实体油
    private Integer vehicleClass;//车辆类型 例如 招商挂靠车
    private Long salary;//主驾补贴
    private Long copilotSalary;//副驾补贴
    private Long driverSwitchSubsidy;//切换司机补贴
    private Float arriveTime;//运行时效
    private Long tenantId;//车队id
    private String logoUrl;
    private Long salarySum;//总补贴
    private String localPhone;//跟单人手机
    private String localUserName;//跟单人名称

    private String reciveTypeName;//回单类型
    private String orderStateName;//订单状态
    private String desRegionName;//到达市
    private String sourceRegionName;//始发市
    private Integer loadNum;//借支数量
    private Integer expenseNum;//报销数量
    private Integer isHis;

    private String source;//始发地详情
    private String des;//目的地详情
    private Long distance;//行驶距离
    private Long carDriverId;//主驾ID
    private Integer paymentWay;//自有车付款方式
    private String paymentWayName;
    private String orderLine;//订单总线路
    private Boolean isTransitLine;//是否有经停城市
    private Integer isReportFee;
    private Integer orderCostReportState;
    private Integer orderCostReportDay;//费用上报失效时间/天
    private LocalDateTime endDate;
    private List<OrderTransitLineInfo> transitLineInfos;

    private Integer arrivePaymentState;
    private Long arrivePaymentFee;
    private String arrivePaymentStateName;
    private Integer isCollection;//是否代收


}
