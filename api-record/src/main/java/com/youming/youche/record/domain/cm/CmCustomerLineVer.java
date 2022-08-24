package com.youming.youche.record.domain.cm;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 客户线路信息表
 * </p>
 *
 * @author 向子俊
 * @since 2022-01-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmCustomerLineVer extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 线路联系电话
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float etcMoney;

    /**
     * 社会油卡金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoney;

    /**
     * 社会车油卡类型（对应静态数据OILCARD_TYPE）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer oilcardType;

    /**
     * 招商车ETC金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float etcMoneyMerchant;

    /**
     * 招商车油卡金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyMerchant;

    /**
     * 招商油卡类型（对应静态数据OILCARD_TYPE）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer oilcardTypeMerchant;

    /**
     * 始发省份ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceProvince;

    /**
     * 始发市编码ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceCity;

    /**
     * 始发县区ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desProvince;

    /**
     * 目的市编码ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desCity;

    /**
     * 目的县区ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer goodsType;

    /**
     * 货物重量（吨）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float goodsWeight;

    /**
     * 货物方数（方）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float goodsVolume;

    /**
     * 支付方式(对应静态数据的 code_type= PAY_WAY)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer payWay;

    /**
     * 招商指导价格（对应单位分）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long guideMerchant;

    /**
     * 社会指导价格（对应单位分）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long guidePrice;

    /**
     * 单价
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal priceUnit;

    /**
     * 单价枚举(对应静态数据的 code_type= PRICE_ENUM)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer priceEnum;

    /**
     * 需求车长(对应静态数据的 code_type= VEHICLE_LENGTH)
     */
    private String vehicleLength;

    /**
     * 需求车辆类型(对应静态数据的 code_type= VEHICLE_STATUS)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer vehicleStatus;

    /**
     * 公里数（KM）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float mileageNumber;

    /**
     * 到达时限（小时）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float arriveTime;

    /**
     * 回单期限（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveTime;

    /**
     * 对账日（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer recondTime;

    /**
     * 开票期限（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceTime;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 事业部
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long divisionDepartment;

    /**
     * 项目部
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long projectDepartment;

    /**
     * 市场部门( 对应组织的组织ID org_type = 1的数据)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long saleDaparment;

    /**
     * 有效开始时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate effectBegin;

    /**
     * 有效结束时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate effectEnd;

    /**
     * 创建人组织ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long orgId;

    /**
     * 创建租户ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tenantId;

    /**
     * 操作人ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long opId;

    /**
     * 收款期限（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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

    /**
     * 社会车线路价格状态（type=CAR_LINE_STATE）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer carLineState;

    /**
     * 招商车线路价格（type＝CAR_LINE_STATE_MERCHANT）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer carLineStateMerchant;

    /**
     * 社会车ETC金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float etcMoneyUpdate;

    /**
     * 社会油卡金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyUpdate;

    /**
     * 招商车ETC金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float etcMoneyMerchantUpdate;

    /**
     * 招商车油卡金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyMerchantUpdate;

    /**
     * 社会车线路状态(修改中)(type=CAR_LINE_STATE)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float carLineStateUpdate;

    /**
     * 招商车线路状态(修改中)(type=CAR_LINE_STATE@MERCHANT)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float carLineStateMerchantUpdate;

    /**
     * 招商指导价格（对应单位分）(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long guideMerchantUpdate;

    /**
     * 社会指导价格（对应单位分）(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long guidePriceUpdate;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long oilcardTypeUpdate;

    /**
     * 招商油卡类型（对应静态数据OILCARD_TYPE）(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
     * 社会实体油卡金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyEntity;

    /**
     * 社会实体油卡金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyEntityUpdate;

    /**
     * 招商车实体油卡金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyEntityMerchant;

    /**
     * 招商车实体油卡金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyEntityMerchantUpdate;

    /**
     * 社会车虚拟油卡金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyVirtual;

    /**
     * 社会车虚拟油卡金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyVirtualUpdate;

    /**
     * 招商车虚拟油卡金额
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyVirtualMerchant;

    /**
     * 招商车虚拟油卡金额(修改中)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyVirtualMerchantUpdate;

    /**
     * 路桥费(分/公里)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long pontagePer;

    /**
     * 油耗(分/公里)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long oilCostPer;

    /**
     * 客户公里数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float etcMoneyContract;

    /**
     * 个体油卡金额比例
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Float oilcardMoneyContract;

    /**
     * 是否回程货
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer backhaulState;

    /**
     * 回程货编号
     */
    private String backhaulNumber;

    /**
     * 往返编码格式:0 自动 1 已有
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer backhaulFormat;

    /**
     * 回程指导价
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long backhaulGuideFee;

    /**
     * 修改操作人
     */
    private Long updateOpId;

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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveTimeDay;

    /**
     * 对账期限日
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer recondTimeDay;

    /**
     * 开票期限日
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceTimeDay;

    /**
     * 收款期限日
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer collectionTimeDay;

    /**
     * 百世工单号
     */
    private Long workId;

}
