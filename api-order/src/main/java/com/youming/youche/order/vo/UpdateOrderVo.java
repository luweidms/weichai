package com.youming.youche.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/5/18 10:51
 */
@Data
public class UpdateOrderVo implements Serializable {

    /**
     * 修改类型：1-修改基础信息  2-修改收入信息  3-修改调度信息  4-修改归属信息
     */
    private Integer updateType;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 靠台时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dependTime;

    /**
     * 公司名称
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
    private String distance;

    /**
     * 承运方公里数
     */
    private String mileageNumber;

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
     * 备注
     */
    private String remark;

    /**
     * 是否为紧急加班车（1 是的 0 不是 ）
     */
    private Integer isUrgent;

    /**
     * 单价
     */
    private Double priceUnit;

    /**
     * 收入信息 单位costPrice
     */
    private Integer priceEnum;

    /**
     * 订单收入
     */
    private String costPrice;

    /**
     * 现付现金金额
     */
    private String prePayCash;

    /**
     * 现付等值卡金额
     */
    private String prePayEquivalenceCardAmount;

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
    private String afterPayCash;

    /**
     * 后付等值卡金额
     */
    private String afterPayEquivalenceCardAmount;

    /**
     * 后付等值卡类型
     */
    private String afterPayEquivalenceCardNumber;

    /**
     * 后付结算类型
     */
    private Integer afterPayAcctType;

    /**
     * 订单收入
     */
    private OrderPaymentDaysInfo incomePaymentDaysInfo;

    /**
     * 派单方式（1 竞价 2 抢单 3 指派）
     */
    private Integer appointWay;

    /**
     * 否需要开票 0无需 1需要
     */
    private Integer isNeedBill;

    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private Integer oilAccountType;

    /**
     * 虚拟油消费对象，1自有油站；2共享油站;
     */
    private Integer oilConsumer;

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
     * 转出的租户id
     */
    private Long toTenantId;

    /**
     * 转单租户的名称
     */
    private String toTenantName;

    /**
     * 账户名称
     */
    private String acctName;

    /**
     * 账户编号
     */
    private String acctNo;

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
     * 副驾驶
     */
    private Long copilotUserId;

    /**
     * 副驾驶姓名
     */
    private String copilotMan;

    /**
     * 副驾驶手机
     */
    private String copilotPhone;

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
     * 路桥费的单价
     */
    private Long pontagePer;

    /**
     * 空驶距离（单位米）
     */
    private Long runWay;

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
     * 路桥费
     */
    private Long pontage;

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
     * 司机补贴天数详情
     */
    private String carDriverSubsidyDate;

    /**
     * 副司机补贴天数详情
     */
    private String copilotSubsidyDate;

    /**
     * 预付虚拟油卡金额
     */
    private Long preOilVirtualFee;

    /**
     * 预付油卡金额
     */
    private Long preOilFee;

    /**
     * 分配油站
     */
    private List<OrderOilDepotScheme> schemes;

    /**
     * 报账模式下添加的油卡
     */
    private List<OrderOilCardInfo> oilCardInfos;

    /**
     * 总费用
     */
    private Long totalFee;

    /**
     * 保费
     */
    private Long insuranceFee;

    /**
     * 指导价格
     */
    private Long guidePrice;

    /**
     * 预付总计金额
     */
    private Long preTotalFee;

    /**
     * 预付现金金额
     */
    private Long preCashFee;

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
     * 到付费用
     */
    private Long arrivePaymentFee;

    /**
     * 预付总计上限比例值
     */
    private Long preTotalScale;

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
     * 到付费用比例
     */
    private Long arrivePaymentFeeScale;

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
     * 是否代收 0 不代收 1 代收
     */
    private Integer isCollection;

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
     * 成本账期
     */
    private OrderPaymentDaysInfo costPaymentDaysInfo;

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

    /**
     * 卡号
     */
    private String oilCardNum;

    /**
     * 始发省
     */
    private Integer sourceProvince;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 始发县
     */
    private Integer sourceCounty;

    /**
     * 到达省
     */
    private Integer desProvince;

    /**
     * 到达市
     */
    private Integer desRegion;

    /**
     * 到达县
     */
    private Integer desCounty;

    /**
     * 现付等值卡类型
     */
    private Integer prePayEquivalenceCardType;
}
