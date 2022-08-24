package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/4/30
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class VehicleOrderDto implements Serializable {
    private Long orderId; //订单号
    private LocalDateTime dependTime; //靠台时间
    private LocalDateTime createTime; //录入时间
    private LocalDateTime carArriveDate;//到达时间
    private String companyName; //客户全称
    private String customNumber;//客户单号
    private String sourceCode; //线路编码
    private String sourceRegion; //始发市
    private String desRegion; //到达市
    private String orderLine;//订单总线路
//    private String reginInfo;//运输路线
    private String lineNode; //线路名称
    private String sourceName; //线路名称
    private String plateNumber; //车牌号码
    private String trailerPlate; //挂车车牌
    private String carDriverMan; //主驾姓名
    private String copilotMan; //副驾姓名
    private String localUserName; //跟单人员
    private String opName; //录入人员
    private String tenantName; //归属部门
    private String goodsInfo; //货物信息
    private String goodsType; //货物类型
    private String goodsTypeName; //货物类型名称
    private Double costPrice;//订单收入
    private Double problemPrice;//异常款
//    private Double incomeExceptionFee;//异常款
    //订单收入合计=订单收入+(异常款)
    private Double totalOrderRevenue;//订单收入合计
    private Double confirmDiffAmount;//对账调整
//    private Double diffFee;
    //确认应收=订单收入合计+对账调整
    private Double notarizeReceivable;//确认应收

    private Double totalFee;//中标价
    private Integer orderState;//订单状态
    private String orderStateName;//订单状态


    private Integer paymentWay;//1:智能模式 2:报账模式 3:承包模式
    private String paymentWayName;//成本模式
    private Double preOilVirtualFee;//预付虚拟油卡金额
    private Double preOilFee;//预付油卡金额
    private Double oilFeeTotal;//油费(预付虚拟油卡金额+预付油卡金额)
    private Double pontage;//路桥费
    private Double preEtcFee;//ETC
    private Double consumeFee;//上报费用
    private Double driverDaySalary;//补贴
    private Double orderFeeExt;//预估成本
    private Double orderCost;//订单成本（直接成本）

    //司机借支
    private Double driverOaLoanAmount;
    //员工借支
    private Double userOaLoanAmount;
    //司机报销
    private Double amount;
    //司机工资
    private Long basicSalaryFee;
    //车辆折旧
    private Double depreciationCost;
    //变动成本=司机借支+员工借支+司机报销+车辆折旧+司机工资
    private Double variableCost;
    //订单毛利=订单收入合计-直接成本-变动成本
    private Double orderMargin;






}
