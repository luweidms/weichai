package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/5/18 10:01
 */
@Data
public class GetOrderDetailForUpdateDto implements Serializable {

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 订单状态0:待确认、1：正在交易、2：已确认、3：在运输、4完成
     */
    private Integer orderState;

    /**
     * 订单状态名称
     */
    private String orderStateName;

    /**
     * 始发省
     */
    private Integer sourceProvince;

    /**
     * 始发省名称
     */
    private String sourceProvinceName;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 始发市名称
     */
    private String sourceRegionName;

    /**
     * 始发县
     */
    private Integer sourceCounty;

    /**
     * 始发县名称
     */
    private String sourceCountyName;

    /**
     * 到达省
     */
    private Integer desProvince;

    /**
     * 到达省名称
     */
    private String desProvinceName;

    /**
     * 到达市
     */
    private Integer desRegion;

    /**
     * 到达市名称
     */
    private String desRegionName;

    /**
     * 到达县
     */
    private Integer desCounty;

    /**
     * 到达县名称
     */
    private String desCountyName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 估算价格状态0:等待2:失败1:成功
     */
    private Integer preAmountFlag;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * 来源订单编号
     */
    private Long fromOrderId;

    /**
     * 转出订单编号
     */
    private Long toOrderId;


    /**
     * 客户名称
     */
    private String customName;

    /**
     * 公司名称（全称）
     */
    private String companyName;

    /**
     * 联系人名称
     */
    private String linkName;

    /**
     * 联系人手机
     */
    private String linkPhone;

    /**
     * 地址
     */
    private String address;

    /**
     * 线路合同URL
     */
    private String clientContractUrl;

    /**
     * 线路合同ID
     */
    private Long clientContractId;

    /**
     * 线路ID
     */
    private Long sourceId;

    /**
     * 线路编码
     */
    private String sourceCode;

    /**
     * 线路名称
     */
    private String sourceName;

    /**
     * 起始地
     */
    private String source;

    /**
     * 目的地
     */
    private String des;

    /**
     * 详细地址
     */
    private String addrDtl;

    /**
     * 目的地详细地址
     */
    private String desDtl;

    /**
     * 地址纬度
     */
    private String nand;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 目的纬度
     */
    private String nandDes;

    /**
     * 目的经度
     */
    private String eandDes;

    /**
     * 文字描述出发地址
     */
    private String navigatSourceLocation;

    /**
     * 文字描述目的地址
     */
    private String navigatDesLocation;

    /**
     * 到达时限
     */
    private Float arriveTime;

    /**
     * 经停点
     */
    private List<OrderTransitLineInfo> transitLineInfos;

    /**
     * 客户公里数
     */
    private Long distance;

    /**
     * 承运方公里数
     */
    private Long mileageNumber;

    /**
     * 货物名称
     */
    private String goodsName;

    /**
     * 货品类型
     */
    private Integer goodsType;

    /**
     * 货物体积
     */
    private Float square;

    /**
     * 货物重量kg
     */
    private Float weight;

    /**
     * 需求车长
     */
    private String vehicleLengh;

    /**
     * 需求车辆类型
     */
    private Integer vehicleStatus;

    /**
     * 客户单号
     */
    private String customNumber;

    /**
     * 收货人
     */
    private String reciveName;

    /**
     * 收货电话
     */
    private String recivePhone;

    /**
     * 回单类型
     */
    private Integer reciveType;

    /**
     * 回单地址的省份
     */
    private Long reciveProvinceId;

    /**
     * 回单地址的省份名称
     */
    private String reciveProvinceName;

    /**
     * 回单地址的市
     */
    private Long reciveCityId;

    /**
     * 回单地址的市名称
     */
    private String reciveCityName;

    /**
     * 回单地址
     */
    private String reciveAddr;

    /**
     * 是否为紧急加班车（1 是的 0 不是 ）
     */
    private Integer isUrgent;

    /**
     * 单价
     */
    private Double priceUnit;

    /**
     * 收入信息 单位
     */
    private Integer priceEnum;

    /**
     * 订单收入
     */
    private Long costPrice;

    /**
     * 现付现金金额
     */
    private Long prePayCash;

    /**
     * 现付等值卡金额
     */
    private Long prePayEquivalenceCardAmount;

    /**
     * 后付等值卡类型
     */
    private Integer afterPayEquivalenceCardType;

    /**
     * 现付等值卡卡号
     */
    private String prePayEquivalenceCardNumber;

    /**
     * 后付现金金额
     */
    private Long afterPayCash;

    /**
     * 后付等值卡金额
     */
    private Long afterPayEquivalenceCardAmount;

    /**
     * 后付等值卡类型
     */
    private String afterPayEquivalenceCardNumber;

    /**
     * 订单收入
     */
    private OrderPaymentDaysInfo incomePaymentDaysInfo;

    /**
     * 成本权限
     */
    private Boolean orderIncomePermission;


    /**
     * 来源租户id
     */
    private Long fromTenantId;

    /**
     * 收入权限
     */
    private Boolean orderCostPermission;

    /**
     * 转出的租户id
     */
    private Long toTenantId;

    /**
     * 转单租户的名称
     */
    private String toTenantName;

    /**
     * 否需要开票 0无需 1需要
     */
    private Integer isNeedBill;

    /**
     * 是否转发：0表示没有转发，1表示转发
     */
    private Integer isTransit;

    /**
     * 是否找回程货：0表示没有，1表示要找回程货
     */
    private Integer isBackhaul;

    /**
     * 回货价格
     */
    private Long backhaulPrice;

    /**
     * 回货联系人名称
     */
    private String backhaulLinkMan;

    /**
     * 空驶距离（单位米）
     */
    private Long runWay;

    /**
     * 油使用方式 1使用客户油;2使用本车队
     */
    private Integer oilUseType;

    /**
     * 回货联系人
     */
    private Long backhaulLinkManId;

    /**
     * 回货联系人电话
     */
    private String backhaulLinkManBill;

    /**
     * 付款方式 1:包干模式 2:实报实销模式 3:承包模式
     */
    private Integer paymentWay;

    /**
     * 载重油耗
     */
    private Float capacityOil;

    /**
     * 空载油耗
     */
    private Float runOil;

    /**
     * 油是否需要发票 0不需要，1需要
     */
    private Integer oilIsNeedBill;

    /**
     * 账户名称
     */
    private String acctName;

    /**
     * 账户编号
     */
    private String acctNo;

    /**
     * 指导价格
     */
    private Long guidePrice;

    /**
     * 保费
     */
    private Long insuranceFee;

    /**
     * 总费用
     */
    private Long totalFee;

    /**
     * 预付总计金额
     */
    private Long preTotalFee;

    /**
     * 预付现金金额
     */
    private Long preCashFee;

    /**
     * 预付虚拟油卡金额
     */
    private Long preOilVirtualFee;

    /**
     * 预付油卡金额
     */
    private Long preOilFee;

    /**
     * 预付ETC金额
     */
    private Long preEtcFee;

    /**
     * 尾款金额
     */
    private Long finalFee;

    /**
     * 预付尾款比例值
     */
    private Long finalScale;

    /**
     * 预付总计上限比例值
     */
    private Long preTotalScale;

    /**
     * 到付费用
     */
    private Long arrivePaymentFee;

    /**
     * 到付费用比例
     */
    private Long arrivePaymentFeeScale;

    /**
     * 预付现金比例值
     */
    private Long preCashScale;

    /**
     * 预付虚拟油卡比例值
     */
    private Long preOilVirtualScale;

    /**
     * 预付油卡比例值
     */
    private Long preOilScale;

    /**
     * 预付ETC比例值
     */
    private Long preEtcScale;

    /**
     * 预付总计上限比例标准
     */
    private Long preTotalScaleStandard;

    /**
     * 预付现金比例标准
     */
    private Long preCashScaleStandard;

    /**
     * 预付虚拟油卡比例标准
     */
    private Long preOilVirtualScaleStandard;

    /**
     * 预付油卡比例标准
     */
    private Long preOilScaleStandard;

    /**
     * 预付ETC比例标准
     */
    private Long preEtcScaleStandard;

    /**
     * 预估成本
     */
    private Long estFee;

    /**
     * 总耗油量
     */
    private Long oilLitreTotal;

    /**
     * 虚拟油量
     */
    private Long oilLitreVirtual;

    /**
     * 实体油量
     */
    private Long oilLitreEntity;

    /**
     * 油单价
     */
    private Long oilPrice;

    /**
     * 主驾驶补贴
     */
    private Long salary;

    /**
     * 副驾驶补贴
     */
    private Long copilotSalary;

    /**
     * 切换司机补贴
     */
    private Long driverSwitchSubsidy;

    /**
     * 路桥费
     */
    private Long pontage;

    /**
     * 补贴具体时间
     */
    private String subsidyTime;

    /**
     * 副驾补贴具体时间
     */
    private String copilotSubsidyTime;

    /**
     * 补贴天数集合
     */
    private List<Map> driverSubsidyDays;

    /**
     * 路桥费的单价
     */
    private Long pontagePer;

    /**
     * 派单方式（1 竞价 2 抢单 3 指派）
     */
    private Integer appointWay;

    /**
     * 是否代收 0 不代收 1 代收
     */
    private Integer isCollection;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车辆
     */
    private Long vehicleCode;

    /**
     * 车长
     */
    private String carLengh;

    /**
     * 代收人手机
     */
    private String collectionUserPhone;

    /**
     * 代收人名称
     */
    private String collectionUserName;

    /**
     * 代收人
     */
    private Long collectionUserId;

    /**
     * 代收人名称
     */
    private String collectionName;

    /**
     * 车辆种类
     */
    private Integer carStatus;

    /**
     * 司机
     */
    private String carDriverMan;

    /**
     * 司机ID
     */
    private Long carDriverId;

    /**
     * 司机手机
     */
    private String carDriverPhone;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 车辆类型名称
     */
    private String vehicleClassName;

    /**
     * 车辆类型(整车/拖头)
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
     * 副驾驶姓名
     */
    private String copilotMan;

    /**
     * 副驾驶手机
     */
    private String copilotPhone;

    /**
     * 副驾驶
     */
    private Long copilotUserId;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人姓名
     */
    private String billReceiverName;

    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;

    /**
     * 调度员
     */
    private Long dispatcherId;

    /**
     * 调度员名字
     */
    private String dispatcherName;

    /**
     * 调度员手机
     */
    private String dispatcherBill;

    /**
     * 值班司机
     */
    private Long onDutyDriverId;

    /**
     * 值班司机名称
     */
    private String onDutyDriverName;

    /**
     * 值班司机手机
     */
    private String onDutyDriverPhone;

    /**
     * 分配油站
     */
    private List<OrderOilDepotScheme> schemes;

    /**
     * 报账模式下添加的油卡
     */
    private List<OrderOilCardInfo> oilCardInfos;

    /**
     * 成本账期
     */
    private OrderPaymentDaysInfo costPaymentDaysInfo;

    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private Integer oilAccountType;

    /**
     * 油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
     */
    private Integer oilBillType;

    /**
     * 虚拟油消费对象，1自有油站；2共享油站;
     */
    private Integer oilConsumer;


    /**
     * 二级组织
     */
    private Long orgId;

    /**
     * 驻场调度人员 id
     */
    private Long localUser;

    /**
     * 驻场人名
     */
    private String localUserName;

    /**
     * 驻场调度手机
     */
    private String localPhone;


}
