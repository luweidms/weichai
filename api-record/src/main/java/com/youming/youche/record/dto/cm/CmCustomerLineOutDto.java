package com.youming.youche.record.dto.cm;

import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor(staticName = "of")
@Accessors(chain = true)
public class CmCustomerLineOutDto implements Serializable {


    private static final long serialVersionUID = -7078110723299450344L;

    /**
     * 客户公里数
     */
    private Float cmMileageNumber;


    /**
     * 公里数（KM）
     */
    private Float mileageNumber;

    /**
     * 支付方式(对应静态数据的 code_type= PAY_WAY)
     */
    private Integer payWay;
    private String payWayName;


    /**
     * 单价枚举(对应静态数据的 code_type= PRICE_ENUM)
     */
    private Integer priceEnum;
    private String priceEnumName;

    /**
     * 需求车长(对应静态数据的 code_type= VEHICLE_LENGTH)
     */
    private String vehicleLength;
    private String vehicleLengthName;

    /**
     * 需求车辆类型(对应静态数据的 code_type= VEHICLE_STATUS)
     */
    private Integer vehicleStatus;
    private String vehicleStatusName;

    /**
     * 始发省份ID
     */
    private Integer sourceProvince;
    private String sourceProvinceName;
    /**
     * 始发市编码ID
     */
    private Integer sourceCity;
    private String sourceCityName;

    /**
     * 始发县区ID
     */
    private Integer sourceCounty;
    private String sourceCountyName;

    /**
     * 目的省份ID
     */
    private Integer desProvince;
    private String desProvinceName;
    /**
     * 目的市编码ID
     */
    private Integer desCity;
    private String desCityName;
    /**
     * 目的县区ID
     */
    private Integer desCounty;
    private String desCountyName;

    /**
     * 起始地
     */
    private String source;
    /**
     * 目的地
     */
    private String des;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 客户等级(对应静态数据的 code_type= CUSTOMER_LEVEL)
     */
    private Integer customerLevel;
    private String customerLevelName;

    /**
     * 税号
     */
    private String einNumber;

    /**
     * 临时线路:公司地址
     */
    private String address;
    /**
     * 回单类型(对应静态数据的 code_type=RECIVE_TYPE)
     */
    private Integer oddWay;
    /**
     * 回单类型名称
     */
    private String oddWayName;

    /**
     * 区域
     */
    private Long area;
    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 发票抬头
     */
    private String lookupName;
    /**
     * 联系手机
     */
    private String linePhone;
    /**
     * 预估应收
     */
    private Long estimateIncome;

    /**
     * 路桥费(分/公里)
     */
    private Long pontagePer;

    /**
     * 油耗(分/公里)
     */
    private Long oilCostPer;
    /**
     * 线路空载油耗
     */
    private Long emptyOilCostPer;
    /**
     * 路桥费
     */
    private Double pontagePerDouble;
    /**
     * 油费
     */
    private Double oilCostPerDouble;
    /**
     * 实体油卡
     */
    private Double emptyOilCostPerDouble;

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
    /**
     * 回程指导价
     */
    private Long backhaulGuideFee;
    private Double backhaulGuideFeeDouble;

    /**
     * 认证状态：1-未认证 2-认证中 3-已认证 4-认证失败
     */
    private Integer authState;
    private String authStateName;

    /**
     * 是否需要审核（1：不需要 2：需要）
     * 0否 1是
     */
    private Integer isAuth;

    /**
     * 回单地址-省
     */
    private Long reciveProvinceId;
    private String reciveProvinceName;
    /**
     * 回单地址-市
     */
    private Long reciveCityId;
    private String reciveCityName;

    /**
     * 回单详细地址
     */
    private String reciveAddress;


    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 司机id
     */
    private Long driverUserId;
    private String driver;
    /**
     * 往返编码
     */
    private String backhaulFormatName;

    /**
     * 审核内容
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
    private Long guideMerchant;
    private Double guideMerchantDouble;
    /**
     * 事业部
     */
    private String divisionDepartment;
    private String divisionDepartmentName;
    /**
     * 社会车油卡类型（对应静态数据OILCARD_TYPE）
     */
    private String oilcardType;
    private String oilcardTypeName;
    /**
     * 招商油卡类型（对应静态数据OILCARD_TYPE）
     */
    private String oilcardTypeMerchant;
    private String oilcardTypeMerchantName;
    /**
     * 市场销售名称
     */
    private String saleName;
    /**
     * 市场销售用户编号
     */
    private Long saleUserId;
    /**
     * 项目部
     */
    private Long projectDepartment;
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
     * 文字描述出发地址，不填写默认使用百度定位地址
     */
    private String navigatSourceLocation;
    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatDesLocation;
    /**
     * 状态（1.有效、2.无效）
     */
    private Integer state;
    /**
     * 状态
     */
    private String stateName;
    /**
     * 招商车线路价格（type＝CAR_LINE_STATE_MERCHANT）
     */
    private Integer carLineStateMerchant;
    private String carLineStateMerchantName;
    /**
     * 社会车线路价格状态（type=CAR_LINE_STATE）
     */
    private Integer carLineState;
    private String carLineStateName;
    /**
     * 不通过原因
     */
    private String advise;
    /**
     * 不通过原因
     */
    private String adviseMerchant;
    /**
     * 利润
     */
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
     * 线路ID
     */
    private Long lineId;
    /**
     * 客户编号
     */
    private Long customerId;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 线路联系人
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
     * 收货电话
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
     * 到达时限（小时）
     */
    private Float arriveTime;
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
    private String saleDaparment;
    private String saleDaparmentName;
    /**
     * 有效开始时间
     */
    private Date effectBegin;
    private String effectBeginName;
    /**
     * 有效结束时间
     */
    private Date effectEnd;
    private String effectEndName;
    /**
     * 操作时间
     */
    private Date opDate;

    /**
     * KA代表
     */
    private String maretSale;
    /**
     * 结算周期(这个未用（使用收款期限（天） 字段）)
     */
    private Integer settleCycle;
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

    /***通过审核的价格***/

    /**
     * 社会车ETC金额
     */
    private Float etcMoney;
    private String etcMoneyDouble;
    /**
     * 社会油卡金额
     */
    private Float oilcardMoney;
    private String oilcardMoneyDouble;
    /**
     * 招商车ETC金额
     */
    private Float etcMoneyMerchant;
    private String etcMoneyMerchantDouble;
    /**
     * 招商车油卡金额
     */
    private Float oilcardMoneyMerchant;
    private String oilcardMoneyMerchantDouble;

    /**
     * 个体合同车ETC金额比例
     */
    private Float etcMoneyContract;
    private String etcMoneyContractDouble;
    /**
     * 个体油卡金额比例
     */
    private Float oilcardMoneyContract;
    private String oilcardMoneyContractDouble;
    /***通过审核的价格***/


    /***修改中的价格***/
    /**
     * 社会车ETC金额(修改中)
     */
    private Float etcMoneyUpdate;
    private String etcMoneyUpdateDouble;
    /**
     * 社会油卡金额(修改中)
     */
    private Float oilcardMoneyUpdate;
    private String oilcardMoneyUpdateDouble;
    /**
     * 招商车ETC金额(修改中)
     */
    private Float etcMoneyMerchantUpdate;
    private String etcMoneyMerchantUpdateDouble;
    /**
     * 招商车油卡金额(修改中)
     */
    private Float oilcardMoneyMerchantUpdate;
    private String oilcardMoneyMerchantUpdateDouble;
    /**
     * 招商指导价格（对应单位分）(修改中)
     */
    private Long guideMerchantUpdate;
    private Double guideMerchantUpdateDouble;
    /**
     * 社会指导价格（对应单位分）(修改中)
     */
    private Long guidePriceUpdate;
    private Double guidePriceUpdateDouble;
    /**
     * 社会车线路状态(修改中)(type=CAR_LINE_STATE)
     */
    private Integer carLineStateUpdate;
    private String carLineStateUpdateName;
    /**
     * 招商车线路状态(修改中)(type=CAR_LINE_STATE@MERCHANT)
     */
    private Integer carLineStateMerchantUpdate;
    private String carLineStateMerchantUpdateName;


    /**
     * 社会车油卡类型（对应静态数据OILCARD_TYPE）(修改中)
     */
    private Integer oilcardTypeUpdate;
    private String oilcardTypeUpdateName;
    /**
     * 招商油卡类型（对应静态数据OILCARD_TYPE）(修改中)
     */
    private Integer oilcardTypeMerchantUpdate;
    private String oilcardTypeMerchantUpdateName;
    /***修改中的价格***/


    /**
     * 操作人名称
     */
    private String opName;

    /**
     * 临时线路:客户简称
     */
    private String customerName;


    /**
     * 回单期限日
     */
    private Integer reciveTimeDay;
    /**
     * 对账期限日
     */
    private Integer recondTimeDay;
    /**
     * 开票期限日
     */
    private Integer invoiceTimeDay;
    /**
     * 收款期限日
     */
    private Integer collectionTimeDay;

    /**
     * 途径点
     */
    public List<CmCustomerLineSubway> subWayList;

    /**
     * 线路名称
     */
    public String lineCodeName;//线路名称
    /**
     * 货品类型
     */
    public Integer goodsType;//货品类型

    /**
     * 货品类型名称
     */
    public String goodsTypeName;

    /**
     * 创建人组织ID
     */
    private Long orgId;
    private String orgName;

    /**
     * 指导价格
     */
    private Long guidePrice;
    /**
     * 指导价格Double
     */
    private Double guidePriceDouble;

    public Double getGuidePriceDouble() {
        if (guidePrice != null) {
            setGuidePriceDouble(CommonUtil.getDoubleFormatLongMoney(guidePrice, 2));
        }
        return guidePriceDouble;
    }

}
