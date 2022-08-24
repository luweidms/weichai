package com.youming.youche.order.vo;

import com.youming.youche.order.dto.subWayDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CustomerInfoVo implements Serializable {


    private static final long serialVersionUID = -1867820620529815773L;

    /**
     * 客户id
     */
    private Long customUserId;


    /**
     * 后付结算类型
     */
    private Integer afterPayAcctType;


    /**
     * 后付等值卡卡号
     */
    private String afterPayEquivalenceCardNumber;


    /**
     * 现付等值卡卡号
     */
    private String prePayEquivalenceCardNumber;

    /**
     * 现付等值卡金额
     */
    private String prePayEquivalenceCardAmount;

    /**
     * 客户单号，多个用换行符分隔
     */
    private String customNumber;
    /**
     * 公司名称(全称)
     */
    private String companyName;
    /**
     * 客户名称
     */
    private String customName;
    /**
     * 联系人
     */
    private String linkName;

    /**
     * 租户联系人电话
     */
    private String linkPhone;
    /**
     * 常用办公地址
     */
    private String address;

    /**
     * 用友编码
     */
    private String yongyouCode;

    private Integer type;

    /**
     * 回单类型
     */
    private Integer reciveType;
    /**
     * 需求车辆类型
     */
    private Integer vehicleStatus;
    /**
     * 需求车长
     */
    private String vehicleLengh;

    /**
     * 货品类型
     */
    private Integer goodsType;
    /**
     * 现付等值卡类型
     */
    private Integer prePayEquivalenceCardType;

    /**
     * 后付等值卡类型
     */
    private Integer afterPayEquivalenceCardType;

    /**
     * 客户公里数
     */
    private double cmMileageNumber;

    /**
     * 供应商公里数
     */
    private double mileageNumber;

    /**
     * 支付方式：1为公司付 2为自付-账户 3为自付微信 4为自付现金
     */
    private Integer payWay;

    /**
     * 收入信息 单位
     */
    private Integer priceEnum;

    /**
     * 车长
     */
    private String vehicleLength;

    /**
     * 始发省份ID
     */
    private Integer sourceProvince;

    /**
     * 始发市编码ID
     */
    private Integer sourceCity;

    /**
     * 始发县区
     */
    private Integer sourceCounty;

    /**
     * 目的省份ID
     */
    private Integer desProvince;

    /**
     * 目的市编码ID
     */
    private Integer desCity;
    /**
     * 到达县
     */
    private Integer desCounty;
    /**
     * 支付方式：1为公司付 2为自付-账户 3为自付微信 4为自付现金
     */
    private String payWayName;
    /**
     * 收入信息 单位
     */
    private String priceEnumName;

    /**
     * 车长
     */
    private String vehicleLengthName;


    private String vehicleStatusName;

    /**
     * 始发省
     */
    private String sourceProvinceName;
    /**
     * 出发城市
     */
    private String sourceCityName;
    /**
     * 始发县名称
     */
    private String sourceCountyName;
    /**
     * 到达省名称
     */
    private String desProvinceName;
    /**
     * 目的城市
     */
    private String desCityName;
    /**
     * 到达县名称
     */
    private String desCountyName;

    private String des;

    private String source;
    /**
     * 客户等级(对应静态数据的 code_type= CUSTOMER_LEVEL)
     */
    private String customerLevelName;

    /**
     * 税号
     */
    private String einNumber;
    /**
     * 回单类型名称
     */
    private String oddWayName;

    private String areaName;


    /**
     * 发票抬头
     */
    private String lookupName;

    /**
     * 联系人电话
     */
    private String linePhone;


    /**
     * 预估应收
     */
    private Long estimateIncome;

    /**
     * 路桥费的单价
     */
    private String pontagePer;


    /**
     * 油耗(分/公里)
     */
    private Long oilCostPer;

    /**
     * 线路空载油耗
     */
    private String emptyOilCostPer;
    /**
     * 路桥费的单价
     */
    private String pontagePerDouble;

    private String oilCostPerDouble;

    private String emptyOilCostPerDouble;

    /**
     * 是否回程货
     */
    private Integer backhaulState;

    /**
     * 回程货编号
     */
    private String backhaulNumber;

    /**
     * 往返编码格式:0 自动 1 已有
     */
    private Integer backhaulFormat;
    //认证
    private String authStateName;
    /**
     * 是否认证
     */
    private Integer isAuth;
    /**
     * 回单地址-省
     */
    private Long reciveProvinceId;

    /**
     * 回单地址-市
     */
    private Long reciveCityId;


    /**
     * 回单详细地址
     */
    private String reciveAddress;
    /**
     * 回单地址的省份名称
     */
    private String reciveProvinceName;
    /**
     * 回单地址的市名称
     */
    private String reciveCityName;
    /**
     * 车牌号
     */
    private String plateNumber;

    private String driver;

    private String backhaulFormatName;

    /**
     * 认证状态：1-未认证 2-已认证 3-认证失败
     */
    private Integer authState;

    /**
     * 审核原因
     */
    private String auditContent;

    /**
     * 对账日（天）
     */
    private Integer recondTime;

    /**
     * 开票期限（天）
     */
    private Integer invoiceTime;

    /**
     * 招商指导价格（对应单位分）
     */
    private String guideMerchant;

    private String guideMerchantDouble;
    /**
     * 事业部
     */
    private String divisionDepartmentName;


    /**
     * 社会车油卡类型（对应静态数据OILCARD_TYPE）
     */
    private Integer oilcardType;

    /**
     * 招商油卡类型（对应静态数据OILCARD_TYPE）
     */
    private Integer oilcardTypeMerchant;
    /**
     * 社会车油卡类型（对应静态数据OILCARD_TYPE）
     */
    private String oilCardTypeName;
    /**
     * 招商油卡类型（对应静态数据OILCARD_TYPE）
     */
    private String oilcardTypeMerchantName;

    /**
     * 市场销售名称
     */
    private String saleName;
    /**
     * 项目部
     */
    private String projectDepartmentName;
    /**
     * 收款期限（天）
     */
    private Integer collectionTime;

    /**
     * 路线编码
     */
    private String lineCodeRule;


    /**
     * 文字描述出发地址，不填写默认使用百度定位地址,做为百度地址的备注
     */
    private String navigatSourceLocation;

    /**
     * 文字描述目的地址，不填写默认使用百度定位地址,做为百度地址的备注
     */
    private String navigatDesLocation;

    private Integer state;

    //状态
    private String stateName;

    private String carLineStateMerchantName;
    /**
     * 社会车线路状态
     */
    private String carLineStateName;

    /**
     * 不通过原因
     */
    private String advise;

    /**
     * 不通过原因
     */
    private String adviseMerchant;

    private String profit;

    private String profitMerchant;

    /**
     * 需要找回货：0-否 1-是
     */
    private Integer isRevertGoods;

    /**
     * 回货联系人
     */
    private String backhaulGuideName;

    /**
     * 回货联系人电话
     */
    private String backhaulGuidePhone;

    /**
     * 指导价格
     */
    private String guidePrice;

    private String guidePriceDouble;

    /**
     * 单价
     */
    private String priceUnit;

    /**
     * 线路ID
     */
    private Long lineId;

    /**
     * 客户单号
     */
    private String customerId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 联系人名称
     */
    private String lineName;

    /**
     * 线路联系手机
     */
    private String lineTel;

    /**
     * 靠台时间（格式 HH:mm）
     */
    private String taiwanDate;


    /**
     * 始发地详细地址
     */
    private String sourceAddress;


    /**
     * 目的地详细地址
     */
    private String desAddress;

    /**
     * 收货人
     */
    private String receiveName;


    /**
     * 收货电话
     */
    private String receivePhone;

    /**
     * 货物名称
     */
    private String goodsName;

    /**
     * 货物重量（吨）
     */
    private Float goodsWeight;

    /**
     * 货物方数（方）
     */
    private Float goodsVolume;


    /**
     * 到达时限
     */
    private Long arriveTime;


    /**
     * 回单期限（天）
     */
    private Integer reciveTime;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 市场部门
     */
    private Long saleDaparment;
    /**
     * 市场部门名称
     */
    private String saleDaparmentName;

    private String effectBeginName;

    private String effectEndName;
    /**
     * 事业部
     */
    private Long divisionDepartment;

    /**
     * KA代表
     */
    private String maretSale;

    /**
     * 合同图片URL
     */
    private String contractUrl;

    /**
     * 合同图片ID
     */
    private Long contractId;


    /**
     * 始发地经度
     */
    private String sourceEand;

    /**
     * 始发地纬度
     */
    private String sourceNand;

    /**
     * 目的地经度
     */
    private String desEand;

    /**
     * 目的地纬度
     */
    private String desNand;

    private String etcMoneyDouble;

    private String oilcardMoneyDouble;

    private String etcMoneyMerchantDouble;

    private String oilcardMoneyMerchantDouble;

    private String etcMoneyContractDouble;

    private String oilcardMoneyContractDouble;

    private String etcMoneyUpdateDouble;

    private String oilcardMoneyUpdateDouble;

    private String etcMoneyMerchantUpdateDouble;

    private String oilcardMoneyMerchantUpdateDouble;

    private String  carLineStateUpdateName;

    private String carLineStateMerchantUpdateName;

    private String oilcardTypeUpdateName;
    /**
     * 招商油卡类型（对应静态数据OILCARD_TYPE）
     */
    private String oilcardTypeMerchantUpdateName;

    private String opName;
    /**
     * 客户简称
     */
    private String customerName;

    private String lineCodeName;

    private String goodsTypeName;

    private Integer sourceRegion;

    private String sourceRegionName;

    /**
     * 到达市
     */
    private Integer desRegion;


    /**
     * 目标地
     */
    private String desRegionName;


    /**
     * 线路ID
     */
    private Long sourceId;

    /**
     * 成本价
     */
    private String costPrice;

    /**
     * 是否为紧急加班车（1 是的 0 不是 ）
     */
    private Integer isUrgent;

    /**
     * 过磅费重量（千克）
     */
    private String weight;

    /**
     * 货物体积
     */
    private Float square;

    /**
     * 创建网点ID
     */
    private Long orgId;

    /**
     * 归属部门
     */
    private String orgName;

    /**
     * 靠台时间
     */
    private String dependTime;

    /**
     * 收货人
     */
    private String reciveName;

    /**
     * 收货电话
     */
    private String recivePhone;

    /**
     * 联系人
     */
    private String contactName;


    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 地址纬度
     */
    private String nand;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 目的经度
     */
    private String eandDes;


    /**
     * 目的纬度
     */
    private String nandDes;

    /**
     * 线路名称
     */
    private String sourceName;

    /**
     * 详细地址
     */
    private String addrDtl;

    /**
     * 目的地详细地址
     */
    private String desDtl;

    /**
     * 回单地址
     */
    private String reciveAddr;

    /**
     * 现付现金金额
     */
    private String prePayCash;



    /**
     * 后付现金金额
     */
    private String afterPayCash;
    /**
     * 后付等值卡金额
     */
    private String afterPayEquivalenceCardAmount;

    private IncomeBalanceDataVo incomeBalanceDataVo;

    private List<subWayDto> subWayListStr;
}
