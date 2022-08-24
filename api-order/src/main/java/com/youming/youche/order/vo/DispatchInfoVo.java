package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DispatchInfoVo implements Serializable {


    private static final long serialVersionUID = 4148066614437257990L;
    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private Integer oilAccountType;
    /**
     * 部门id
     */
    private Integer orgId;

    /**
     * 代理手机号
     */
    private String agentPhone;

    /**
     * 实体油量
     */
    private String oilLitreEntity;

    /**
     * 虚拟油量
     */
    private String oilLitreVirtual;
    /**
     * 总耗油量
     */
    private String oilLitreTotal;

    /**
     * 主驾补贴天数
     */
    private String carDriverSubsidyDate;

    /**
     * 副驾驶补贴天数
     */
    private String copilotSubsidyDate;

    /**
     * 副驾驶补贴
     */
    private String copilotSubsidy;

    /**
     * 用户补贴
     */
    private String userSubsidy;


    /**
     * 预估成本
     */
    private String estFee;

    /**
     * 路桥费
     */
    private String pontage;

    /**
     * 油单价
     */
    private String oilPrice;


    /**
     * 油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
     */
    private Integer oilBillType;


    /**
     * 账单接收人用户编号
     */
    private String billReceiverUserId;


    /**
     * 账单接收人姓名
     */
    private String billReceiverName;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 代理人id
     */
    private String agentId;

    /**
     * 代理账户名称
     */
    private String agentAccountName;

    private String oilDepotNandThree;

    private String oilDepotEandThree;

    private String oilDepotNandTwo;

    private String oilDepotEandTwo;

    private String oilDepotEandOne;

    private String oilDepotNandOne;


    private String dependDistanceOne;


    /**
     * 副驾驶手机
     */
    private String copilotPhone;


    /**
     * 副驾驶姓名
     */
    private String copilotMan;


    /**
     * 副驾驶id
     */
    private Long copilotUserId;


    /**
     * 订单号
     */
    private Long orderId;


    private String oilSelfVirtual;


    private String oilSelfEntity;



    /**
     * 油使用方式 1使用客户油;2使用本车队
     */
    private Integer oilUseType;

    /**
     * 回货联系人id
     */
    private Long backhaulLinkManId;


    /**
     * 回货联系人名称
     */
    private String backhaulLinkMan;

    /**
     * 回货联系人电话
     */
    private String backhaulLinkManBill;

    /**
     * 回货价格
     */
    private String backhaulPrice;

    /**
     * 空驶距离（单位米）
     */
    private String runWay;

    /**
     * 付款方式 1:包干模式 2:实报实销模式 3:承包模式
     */
    private Integer paymentWay;
    /**
     * 是否找回程货：0表示没有，1表示要找回程货
     */
    private Integer isBackhaul;

    private String isAgent;

    private Long workId;

    /**
     * 转出的租户id
     */
    private Long toTenantId;

    /**
     * 订单状态0:待确认、1：正在交易、2：已确认、3：在运输、4完成
     */
    private Integer orderState;
    /**
     * 回单地址
     */
    private String reciveAddr;
    /**
     * 派单方式（1 竞价 2 抢单 3 指派）
     */
    private Integer appointWay;
    /**
     * 是否需要开票 0无需 1需要
     */
    private Integer isNeedBill;
    /**
     * 转出订单编号
     */
    private Long toOrderId;
    /**
     * 驻场调度人员 id
     */
    private Long localUser;
    /**
     * 驻场人名
     */
    private String localUserName;

    /**
     * 来源租户id
     */
    private Long fromTenantId;
    /**
     * 驻场调度手机
     */
    private String localPhone;

    private Double maxAmount;

    private Integer oilDepotIdOne;

    private String oilDepotNameOne;

    private String oilDepotFeeOne;


    private String oilDepotPriceOne;

    private String oilDepotLitreOne;

    private String oilDepotIdTwo;

    private String oilDepotNameTwo;

    private String oilDepotFeeTwo;

    private String dependDistanceTwo;

    private String oilDepotPriceTwo;

    private String oilDepotLitreTwo;

    private Integer oilDepotIdThree;

    private String oilDepotNameThree;

    private String oilDepotFeeThree;

    private  String dependDistanceThree;

    private String oilDepotPriceThree;

    private  String oilDepotLitreThree;
    /**
     * 虚拟油消费对象，1自有油站；2共享油站;
     */
    private Integer oilConsumer;
    /**
     * 司机id
     */
    private Long carDriverId;
    /**
     * 司机
     */
    private String carDriverMan;

    /**
     * 司机手机
     */
    private String carDriverPhone;
    /**
     * 车长
     */
    private String vehicleLengthName;

    /**
     * 车辆类型
     */
    private String vehicleStatusName;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 车辆类别 1自有公司车 2招商挂靠车 3临时外调车 4外来挂靠车 5外调合同车
     */
    private String vehicleClassName;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;
    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    /**
     * 挂车ID
     */
    private Long trailerId;

    /**
     * 挂车车牌
     */
    private String trailerPlate;

    /**
     * 车牌号
     */
    private String plateNumber;

    private String emptyOilCostPerY;

    private String oilCostPerY;
    /**
     * 账户名称
     */
    private String acctName;
    /**
     * 账户编号
     */
    private String acctNo;
    /**
     * 转单租户的名称
     */
    private String toTenantName;
    /**
     * 租户名称(车队全称)
     */
    private String tenantName;
    /**
     * 租户id
     */
    private Long tenantId;

    private Integer appointWayChangeValue;

    /**
     * 车长
     */
    private String vehicleLength;

    /**
     * 需求车辆类型
     */
    private Integer vehicleStatus;

    /**
     * 是否使用车辆油耗
     */
    private String isUseCarOilCost;
    /**
     * 线路空载油耗
     */
    private String emptyOilCostPer;

    /**
     * 油耗(分/公里)
     */
    private String oilCostPer;

    /**
     * 路桥费的单价
     */
    private String pontagePer;

    /**
     * 总费用
     */
    private String totalFee;

    /**
     * 保费
     */
    private String insuranceFee;

    /**
     * 预付总计上限比例标准
     */
    private String preTotalScaleStandard;

    /**
     * 预付虚拟油卡比例标准
     */
    private String preOilVirtualScaleStandard;

    /**
     * 预付油卡比例标准
     */
    private String preOilScaleStandard;

    /**
     * 预付ETC比例标准
     */
    private String preEtcScaleStandard;

    /**
     * 预付现金比例标准
     */
    private String preCashScaleStandard;

    /**
     * 预付总计上限比例值
     */
    private String preTotalScale;


    /**
     * 预付尾款比例值
     */
    private String finalScale;

    /**
     * 预付现金比例值
     */
    private String preCashScale;
    /**
     * 预付虚拟油卡比例值
     */
    private String preOilVirtualScale;

    /**
     * 预付油卡比例值
     */
    private String preOilScale;

    /**
     * 预付ETC比例值
     */
    private String preEtcScale;

    /**
     * 指导价格
     */
    private String guidePrice;

    /**
     * 到付费用比例
     */
    private String arrivePaymentFeeScale;

    /**
     * 账期
     */
    private Integer paymentDays;

    /**
     * 预付现金金额
     */
    private String preCashFee;

    /**
     * 预付总计金额
     */
    private String preTotalFee;

    /**
     * 尾款金额
     */
    private String finalFee;

    /**
     * 预付虚拟油卡金额
     */
    private String preOilVirtualFee;


    /**
     * 预付油卡金额
     */
    private String preOilFee;

    /**
     * 预付ETC金额
     */
    private String preEtcFee;

    /**
     * 到付费用
     */
    private String arrivePaymentFee;

    private String finalCashFee;
    /**
     * 备注
     */
    private String remark;

    private DispatchBalanceDataVo dispatchBalanceDataVo;


    private List<OrderOilCardInfoVo> oilCardStr;
}
