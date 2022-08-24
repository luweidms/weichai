package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class CmCustomerInfoOutDto implements Serializable {
    /**
     * 客户编号
     */
    private String customerId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 客户简称
     */
    private String customerName;
    /**
     * 地址
     */
    private String address;
    /**
     * 联系人名称
     */
    private String lineName;
    /**
     * 联系电话
     */
    private String lineTel;
    /**
     * 联系人电话
     */
    private String linePhone;
    /**
     * 市场部门
     */
    private String saleDaparment;
    private String saleDaparmentName;
    /**
     * 对账日（天）
     */
    private Integer recondTime;
    /**
     * 开票期限（天）
     */
    private Integer invoiceTime;
    /**
     * 回单期限（天）
     */
    private Integer reciveTime;
    /**
     * 收款期限
     */
    private Integer collectionTime;
    /**
     * 客户归类
     */
    private String customerCategory;
    /**
     * 用友编码
     */
    private String yongyouCode;
    /**
     * 后付结算类型
     */
    private Integer afterPayAcctType;
    /**
     * 车长
     */
    private Integer carLengh;
    /**
     * 车辆种类
     */
    private Integer carStatus;
    /**
     * 现付等值卡类型
     */
    private Integer prePayEquivalenceCardType;
    /**
     * 后付等值卡类型
     */
    private Integer afterPayEquivalenceCardType;
    /**
     * 回单类型
     */
    private Integer reciveType;
    /**
     * 收入信息 单位
     */
    private Integer priceEnum;
    /**
     * 事业部
     */
    private Integer divisionDepartment;
    /**
     * 项目部
     */
    private Integer projectDepartment;
    /**
     * 单价
     */
    private Long priceUnit;
    private Double priceUnitDouble;

    /**
     * 预估应收
     */
    private Float estimateIncome;
    /**
     * 线路ID
     */
    private Long lineId;
    //	private Long customerId;
    /**
     * 合同编号
     */
    private String contractNo;
    //	private String lineName;
//	private String lineTel;
    /**
     * 靠台时间（格式 HH:mm）
     */
    private String taiwanDate;
    //	private String maretSale;
    /**
     * 结算周期天(这个未用（使用收款期限（天） 字段）)
     */
    private Integer settleCycle;
    /**
     * 合同图片url
     */
    private String contractUrl;
    /**
     * 图片id
     */
    private Long contractId;
    /**
     * etc金额
     */
    private Float etcMoney;
    /**
     * 油卡金额
     */
    private Float oilcardMoney;
    private Float etcMoneyMerchant;
    private Float oilcardMoneyMerchant;
    /**
     * 始发省
     */
    private Integer sourceProvince;
    private Integer sourceCity;
    private Integer sourceCounty;
    private String sourceEand;
    private String sourceNand;
    private String sourceAddress;
    private Integer desProvince;
    /**
     * 目的城市
     */
    private Integer desCity;
    /**
     * 到达县
     */
    private Integer desCounty;
    /**
     * 目的导航经度
     */
    private String desEand;
    /**
     * 目的地纬度
     */
    private String desNand;
    /**
     * 目的地详细地址
     */
    private String desAddress;
    private String receiveName;
    private String receivePhone;
    private String goodsName;
    private Float goodsWeight;
    private Float goodsVolume;
    //	private Integer payWay;
    private Long guidePrice;
    //	private Long priceUnit;
//	private Integer priceEnum;
    private String vehicleLength;
    private Integer vehicleStatus;
    /**
     * 承运方公里数
     */
    private Float mileageNumber;
    /**
     * 到达时限
     */
    private Float arriveTime;
    //	private Integer reciveTime;
    /**
     * 备注
     */
    private String remarks;
    //	private Long divisionDepartment;
//	private Long projectDepartment;
//	private Long saleDaparment;
    /**
     * 有效开始时间
     */
    private Date effectBegin;
    /**
     * 有效結束时间
     */
    private Date effectEnd;
    /**
     * 部门id
     */
    private Long orgId;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 操作时间
     */
    private Date opDate;
    /**
     * 操作人id
     */
    private Long opId;
    //	private Integer recondTime;
//	private Integer invoiceTime;
//	private Integer collectionTime;
    /**
     * 招商指导价格（对应单位分） 承包价
     */
    private Long guideMerchant;
    /**
     * 社会车油卡类型（对应静态数据OILCARD_TYPE）
     */
    private Integer oilcardType;
    /**
     * 线路信息:线路编号
     */
    private String lineCodeRule;
    /**
     * 文字描述出发地址，不填写默认使用百度定位地址,做为百度地址的备注
     */
    private String navigatSourceLocation;
    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatDesLocation;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 招商车线路状态
     */
    private Integer carLineStateMerchant;
    /**
     * 社会车线路状态
     */
    private Integer carLineState;
    /**
     * 社会车ETC金额(修改中)
     */
    private Float etcMoneyUpdate;
    /**
     * 社会油卡金额(修改中)
     */
    private Float oilcardMoneyUpdate;
    /**
     * 招商车ETC金额(修改中)
     */
    private Float etcMoneyMerchantUpdate;
    /**
     * 招商车油卡金额(修改中)
     */
    private Float oilcardMoneyMerchantUpdate;
    /**
     * 社会车线路状态
     */
    private Integer carLineStateUpdate;
    /**
     * 招商车线路状态
     */
    private Integer carLineStateMerchantUpdate;
    /**
     * 招商指导价格
     */
    private Long guideMerchantUpdate;
    /**
     * 社会指导价格
     */
    private Long guidePriceUpdate;
    /**
     * 社会车油卡类型
     */
    private Integer oilcardTypeUpdate;
    /**
     * 招商油卡类型
     */
    private Integer oilcardTypeMerchantUpdate;
    /**
     * 利潤
     */
    private String profit;
    /**
     * 招商车毛利润
     */
    private String profitMerchant;
    /**
     * 不通过原因
     */
    private String advise;
    /**
     * 不通过原因
     */
    private String adviseMerchant;
    /**
     * 目的地
     */
    private String des;
    /**
     * 起始地
     */
    private String source;
    //  private Float estimateIncome;
    /**
     * 招商油卡类型
     */
    private String oilcardTypeMerchant;
    /**
     * 市场销售名称
     */
    private String saleName;
    /**
     * 市场销售用户编号
     */
    private Long saleUserId;
    /**
     * 油耗
     */
    private Long oilCostPer;
    /**
     * 路桥费的单价
     */
    private Long pontagePer;
    private Double oilCostPerDouble;
    private Double pontagePerDouble;
    /**
     * 油站ID
     */
    private Long oilId;
    /**
     * 时效罚款规则(对应静态数据 code_type=LAST_FEE_RULES 默认为4其他客户)
     */
    private String ageFineRule;
    /**
     * 客户编码
     */
    private String customerCode;
    /**
     * 认证状态：1-未认证  2-已认证 3-认证失败
     */
    private Integer authState;
    private String authStateString;

    /**
     * 状态
     */
    private String stateString;
    /**
     * 审核原因
     */
    private String auditContent;
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
     * 类型
     */
    private Integer type;
    /**
     * 对账期限
     */
    private Integer reconciliationTime;
    /**
     * 对账期限月
     */
    private Integer reconciliationMonth;
    /**
     * 对账期限日
     */
    private Integer reconciliationDay;
    /**
     * 回单期限月
     */
    private Integer reciveMonth;
    /**
     * 回单期限日
     */
    private Integer reciveDay;
    /**
     * 开票月份
     */
    private Integer invoiceMonth;
    private Integer invoiceDay;
    /**
     * 收款期限月
     */
    private Integer collectionMonth;
    /**
     * 收款期限日
     */
    private Integer collectionDay;
    /**
     * 发票抬头
     */
    private String lookupName;
    /**
     * 客户等级(对应静态数据的 code_type= CUSTOMER_LEVEL)
     */
    private String customerLevel;
    /**
     * 客户等级名称
     */
    private String customerLevelName;
    /**
     * 线路编码
     */
    private String lineNumber;
    /**
     * 税号
     */
    private String einNumber;
    /**
     * 支付方式：1为公司付 2为自付-账户 3为自付微信 4为自付现金
     */
    private String payWay;
    /**
     * KA代表
     */
    private String maretSale;
    /**
     * 支付方式名称
     */
    private String payWayName;
    /**
     * 回单类型(对应静态数据的 code_type=RECIVE_TYPE)
     */
    private Integer oddWay;
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

}
