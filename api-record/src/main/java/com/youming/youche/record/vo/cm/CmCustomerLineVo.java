package com.youming.youche.record.vo.cm;

import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 客户线路信息表
 * 入参
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
public class CmCustomerLineVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 线路ID
	 */
	private Long id;

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
	 * KA代表
	 */
	private String maretSale;

	/**
	 * 市场销售用户编号
	 */
	private Long saleUserId;

	/**
	 * 市场销售名称
	 */
	private String saleName;

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
	 * 社会车ETC金额
	 */
	private Float etcMoney;

	/**
	 * 社会油卡金额
	 */
	private Float oilcardMoney;

	/**
	 * 社会车油卡类型（对应静态数据OILCARD_TYPE）
	 */
	private Integer oilcardType;

	/**
	 * 招商车ETC金额
	 */
	private Float etcMoneyMerchant;

	/**
	 * 招商车油卡金额
	 */
	private Float oilcardMoneyMerchant;

	/**
	 * 招商油卡类型（对应静态数据OILCARD_TYPE）
	 */
	private Integer oilcardTypeMerchant;

	/**
	 * 始发省份ID
	 */
	private Integer sourceProvince;

	/**
	 * 始发市编码ID
	 */
	private Integer sourceCity;

	/**
	 * 始发县区ID
	 */
	private Integer sourceCounty;

	/**
	 * 始发地经度
	 */
	private String sourceEand;

	/**
	 * 始发地纬度
	 */
	private String sourceNand;

	/**
	 * 始发地详细地址
	 */
	private String sourceAddress;

	/**
	 * 目的省份ID
	 */
	private Integer desProvince;

	/**
	 * 目的市编码ID
	 */
	private Integer desCity;

	/**
	 * 目的县区ID
	 */
	private Integer desCounty;

	/**
	 * 目的地经度
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

	/**
	 * 收货人
	 */
	private String receiveName;

	/**
	 * 收货电话
	 */
	private String receivePhone;

	/**
	 * 货品名
	 */
	private String goodsName;

	/**
	 * 货品类型
	 */
	private Integer goodsType;

	/**
	 * 货品类型名称
	 */
	private String goodsTypeName;

	/**
	 * 货物重量（吨）
	 */
	private Float goodsWeight;

	/**
	 * 货物方数（方）
	 */
	private Float goodsVolume;

	/**
	 * 支付方式(对应静态数据的 code_type= PAY_WAY)
	 */
	private Integer payWay;

	/**
	 * 招商指导价格（对应单位分）
	 */
	private Long guideMerchant;

	/**
	 * 社会指导价格（对应单位分）
	 */
	private Long guidePrice;

	/**
	 * 单价
	 */
	private BigDecimal priceUnit;

	/**
	 * 单价枚举(对应静态数据的 code_type= PRICE_ENUM)
	 */
	private Integer priceEnum;

	/**
	 * 需求车长(对应静态数据的 code_type= VEHICLE_LENGTH)
	 */
	private String vehicleLength;

	/**
	 * 需求车辆类型(对应静态数据的 code_type= VEHICLE_STATUS)
	 */
	private Integer vehicleStatus;

	/**
	 * 公里数（KM）
	 */
	private Float mileageNumber;

	/**
	 * 到达时限（小时）
	 */
	private Float arriveTime;

	/**
	 * 回单期限（天）
	 */
	private Integer reciveTime;

	/**
	 * 对账日（天）
	 */
	private Integer recondTime;

	/**
	 * 开票期限（天）
	 */
	private Integer invoiceTime;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * 事业部
	 */
	private Long divisionDepartment;

	/**
	 * 项目部
	 */
	private Long projectDepartment;

	/**
	 * 市场部门( 对应组织的组织ID org_type = 1的数据)
	 */
	private Long saleDaparment;
	private String saleDaparmentName;

	/**
	 * 有效开始时间
	 */
	private LocalDate effectBegin;

	/**
	 * 有效结束时间
	 */
	private LocalDate effectEnd;

	/**
	 * 创建人组织ID
	 */
	private Long orgId;

	/**
	 * 创建租户ID
	 */
	private Long tenantId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 操作时间
	 */
	private LocalDateTime updateTime;

	/**
	 * 操作人ID
	 */
	private Long opId;

	/**
	 * 收款期限（天）
	 */
	private Integer collectionTime;

	/**
	 * 文字描述目的地址，不填写默认使用百度定位地址
	 */
	private String navigatDesLocation;

	/**
	 * 文字描述出发地址，不填写默认使用百度定位地址
	 */
	private String navigatSourceLocation;

	/**
	 * 路线编码
	 */
	private String lineCodeRule;

	/**
	 * 线路名称
	 */
	private String lineCodeName;

	/**
	 * 路线状态 1：有效；2无效
	 */
	private Integer state;
	private String stateName;

	/**
	 * 社会车线路价格状态（type=CAR_LINE_STATE）
	 */
	private Integer carLineState;

	/**
	 * 招商车线路价格（type＝CAR_LINE_STATE_MERCHANT）
	 */
	private Integer carLineStateMerchant;

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
	 * 社会车线路状态(修改中)(type=CAR_LINE_STATE)
	 */
	private Float carLineStateUpdate;

	/**
	 * 招商车线路状态(修改中)(type=CAR_LINE_STATE@MERCHANT)
	 */
	private Float carLineStateMerchantUpdate;

	/**
	 * 招商指导价格（对应单位分）(修改中)
	 */
	private Long guideMerchantUpdate;

	/**
	 * 社会指导价格（对应单位分）(修改中)
	 */
	private Long guidePriceUpdate;

	/**
	 * 社会车油卡类型（对应静态数据OILCARD_TYPE）(修改中)
	 */
	private Long oilcardTypeUpdate;

	/**
	 * 招商油卡类型（对应静态数据OILCARD_TYPE）(修改中)
	 */
	private Long oilcardTypeMerchantUpdate;

	private String profit;

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
	 * 油站id
	 */
	private Long oilId;

	/**
	 * 社会实体油卡金额
	 */
	private Float oilcardMoneyEntity;

	/**
	 * 社会实体油卡金额(修改中)
	 */
	private Float oilcardMoneyEntityUpdate;

	/**
	 * 招商车实体油卡金额
	 */
	private Float oilcardMoneyEntityMerchant;

	/**
	 * 招商车实体油卡金额(修改中)
	 */
	private Float oilcardMoneyEntityMerchantUpdate;

	/**
	 * 社会车虚拟油卡金额
	 */
	private Float oilcardMoneyVirtual;

	/**
	 * 社会车虚拟油卡金额(修改中)
	 */
	private Float oilcardMoneyVirtualUpdate;

	/**
	 * 招商车虚拟油卡金额
	 */
	private Float oilcardMoneyVirtualMerchant;

	/**
	 * 招商车虚拟油卡金额(修改中)
	 */
	private Float oilcardMoneyVirtualMerchantUpdate;

	/**
	 * 客户公里数
	 */
	private Float cmMileageNumber;

	/**
	 * 临时线路:用友编号
	 */
	private String yongyouCode;

	/**
	 * 临时线路:客户简称
	 */
	private String customerName;

	/**
	 * 临时线路:公司地址
	 */
	private String address;

	/**
	 * 线路空载油耗
	 */
	private Long emptyOilCostPer;

	/**
	 * 个体合同车ETC金额比例
	 */
	private Float etcMoneyContract;

	/**
	 * 个体油卡金额比例
	 */
	private Float oilcardMoneyContract;

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

	/**
	 * 认证状态：1-未认证 2-认证中 3-已认证 4-认证失败
	 */
	private Integer authState;

	/**
	 * 审核内容
	 */
	private String auditContent;

	/**
	 * 0否 1是
	 */
	private Integer isAuth;

	/**
	 * 回货联系人
	 */
	private String backhaulGuideName;

	/**
	 * 回货联系人电话
	 */
	private String backhaulGuidePhone;

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
	 * 绑定车牌号码
	 */
	private String plateNumber;

	/**
	 * 司机用户编号
	 */
	private Long driverUserId;

	/**
	 * 需要找回货：0-否 1-是
	 */
	private Integer isRevertGoods;

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
	 * 回单期限月
	 */
	private Integer reciveTimeMonth;

	/**
	 * 对账期限月
	 */
	private Integer recondTimeMonth;

	/**
	 * 开票期限月
	 */
	private Integer invoiceTimeMonth;

	/**
	 * 收款期限月
	 */
	private Integer collectionTimeMonth;
	/**
	 * 百世工单号
	 */
	private Long workId;

	/**线路关键字*/
	private String lineKeyWord;

	/**
	 * 客户名称
	 */
	private String companyName;

	/**
	 * 有效期
	 */
	private String validDays;
	//出发地址
	private String sourceProvinceAndCity;
	//应到地址
	private String desProvinceAndCity;
	//经停点
	private List<CmCustomerLineSubway> lineSubwayList;

	private String source;
	private String des;

	//车型要求
	private String vehicleType;
	//失败原因
	private String reasonFailure;
}
