package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleDataInfoVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

//
//    // @TableId(value = "his_id", type = IdType.AUTO)
//    private Long hisId;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 用户ID/发布人用户编号(司机归属用户ID)
     */
    private Long userId;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 车长
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.VARCHAR)
    private String vehicleLength;

    /**
     * 车辆载重
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.VARCHAR)
    private String vehicleLoad;

    /**
     * 车辆住址
     */
    private String vehicleAddress;

    /**
     * 车辆功能
     */
    private Integer vehicleType;

    /**
     * 车辆品牌
     */
    private String brandModel;

    /**
     * 货箱结构
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.INTEGER)
    private Integer vehicleStatus;

    /**
     * 随车手机
     */
    private String contactNumber;

    /**
     * 司机名称
     */
    private String linkman;

    /**
     * 身份证
     */
    private String identifcationCard;

    /**
     * 设备编码
     */
    private String equipmentCode;


    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 证件类型 1、表示身份证 2、表示驾驶证
     */
    private Integer idType;

    /**
     * 设备卡号码
     */
    private String equipmentCardnb;

    /**
     * 设备授权号码
     */
    private String equipmentEmpowrnb;

    /**
     * 设备设置IP指令
     */
    private String equipmentSetcommand;

    /**
     * 设备名称
     */
    private String equipmentName;

    /**
     * 车辆图片
     */
    private Long vehiclePicture;

    /**
     * 定位服务:0-无；1-G7；2-app定位; 3-易流
     */
    private Integer locationServ;

    /**
     * 拖挂及轮轴
     */
    private Integer vehicleTariler;

    /**
     * 行驶证
     */
    private String drivingLicense;

    /**
     * 驾驶证
     */
    private Long adrivingLicense;

    /**
     * 运营证
     */
    private String operCerti;

    /**
     * 常跑路线
     */
    private String oftenRun;

    /**
     * 常跑省份
     */
    private String oftenRpovince;

    /**
     * 常跑地市
     */
    private String oftenRegion;

    /**
     * 常跑县区
     */
    private String oftenCountry;

    /**
     * 轻货方数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.VARCHAR)
    private String lightGoodsSquare;

    /**
     * 审核状态
     */
    private Integer verifySts;

    /**
     * 省份证图片id
     */
    private Long idenPicture;

    /**
     * 车辆图片url
     */
    private String vehiclePicUrl;

    /**
     * 身份证图片url
     */
    private String identifcationCardUrl;

    /**
     * 行驶证图片url
     */
    private String drivingLicenseUrl;

    /**
     * 驾驶证图片url
     */
    private String adriverLicenseUrl;

    /**
     * 运营证图片url
     */
    private String operCertiUrl;

    /**
     * 用户归属的二级组织
     */
    private Long orgId;

    /**
     * 用户归属的顶级组织
     */
    private Long rootOrgId;

    /**
     * 审核原因
     */
    private String verifyReason;

    /**
     * 审核时间
     */
    private LocalDateTime auditDate;

    /**
     * 标识数据来源，0、表示平台发展1、表示爬虫2、表示百度3、表示自动生成4、表示外购
     */
    private Integer sourceFlag;

    /**
     * 用户是否接单:1不接单，2接单
     */
    private Integer isWorking;

    /**
     * 司机用户编号
     */
    private Long driverUserId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 管理人员备注
     */
    private String managerRemark;

    /**
     * 车辆类别
     */
    private Integer vehicleClass;

    /**
     * 车架号
     */
    private String vinNo;

    /**
     * 油耗
     */
    private String oilWear;

    /**
     * 车主
     */
    private String carOwner;

    /**
     * 车主手机
     */
    private String carPhone;

    /**
     * 车辆图片是否通过
     */
    private Integer vehiclePictureState;

    /**
     * 行使证是否通过
     */
    private Integer drivingLicenseState;

    /**
     * 运营范围
     */
    private String carRemark;

    /**
     * 租金
     */
    private Long vehicleRent;

    /**
     * 管理费
     */
    private Long managementCost;

    /**
     * 代收保险
     */
    private Long collectionInsurance;

    /**
     * 车贷
     */
    private Long carLoan;

    /**
     * 结算时间
     */
    private Integer settlementTime;

    /**
     * 挂靠或者托管（1：挂靠，2：托管）
     */
    private Integer affilateCustodian;

    /**
     * etc卡号
     */
    private String etcCardNumber;

    /**
     * etc金额(单位分)
     */
    private Long etcAmount;

    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    /**
     * 路歌返回信息
     */
    private String retResult;

    /**
     * 路歌接口调用时间
     */
    private LocalDateTime retDate;

    /**
     * 路歌返回不通过原因:根据数字位数标识含义，每位数字含义参见
     */
    private String retDetail;

    /**
     * 车辆隶属关系
     */
    private String vehicleAffiliation;

    /**
     * 发动机号
     */
    private String engineNo;

    /**
     * 申请状态 0未申请 1申请加入托管 2申请取消托管
     */
    private Integer custodialPendingsStatus;

    /**
     * 保险费
     */
    private Long insuranceFee;

    /**
     * 轮胎费
     */
    private Long tyreFee;

    /**
     * 审车费
     */
    private Long examVehicleFee;

    /**
     * 保养费
     */
    private Long maintainFee;

    /**
     * 维修费
     */
    private Long repairFee;

    /**
     * 其他费
     */
    private Long otherFee;

    /**
     * 购买时间
     */
    private LocalDateTime purchaseDate;

    /**
     * 折旧月数
     */
    private Integer depreciatedMonth;

    /**
     * 归属大区ID
     */
    private Long attachedRootOrgId;

    /**
     * 价格
     */
    private Long price;

    /**
     * 归属省份
     */
    private Long attachedProvince;

    /**
     * 归属地区
     */
    private Long attachedRegion;

    /**
     * 归属地
     */
    private String attachedAddr;

    /**
     * 上次保养时间
     */
    private LocalDateTime prevMaintainTime;

    /**
     * 保险时间
     */
    private LocalDateTime insuranceTime;

    /**
     * 季审时间
     */
    private LocalDateTime seasonalVeriTime;

    /**
     * 年审时间
     */
    private LocalDateTime annualVeriTime;

    /**
     * 保险单号
     */
    private String insuranceCode;

    /**
     * 保养周期（公里）
     */
    private Long maintainDis;

    /**
     * 保养预警公里
     */
    private Long maintainWarnDis;

    /**
     * 版本
     */
    private String carVersion;

    /**
     * 空载油耗(升/百公里)
     */
    private Long loadEmptyPilCost;

    /**
     * 载重油耗(升/百公里)
     */
    private Long loadFullPilCost;

    /**
     * 归属运营部门
     */
    private Long attachedRootOrgTwoId;

    /**
     * 是否使用车辆油耗
     */
    private Integer isUserCarOilCost;

    /**
     * 上一单单号
     */
    private Long beforeOrder;

    /**
     * 当前单号
     */
    private Long currOrder;

    /**
     * 归属人
     */
    private String attachedMan;

    /**
     * 归属人ID
     */
    private String attachedManId;

    /**
     * 归属人手机号
     */
    private String attachedManPhone;

    /**
     * 会员类型
     */
    private Integer carUserType;

    /**
     * 贷款利息
     */
    private Long loanInterest;

    /**
     * 还款期数
     */
    private Integer interestPeriods;

    /**
     * 已还款期数
     */
    private Integer payInterestPeriods;

    /**
     * 登记日期
     */
    private LocalDateTime registrationTime;

    /**
     * 登记证号
     */
    private Long registrationNumble;

    /**
     * 行驶证有效期
     */
    private LocalDate vehicleValidityTime;

    /**
     * 营运证有效期
     */
    private LocalDate operateValidityTime;

    /**
     * 证照信息备注
     */
    private String remarkForVailidity;

    /**
     * 合同图片ID
     */
    private Long contractPicId;

    /**
     * 合同图片URL
     */
    private String contractPicUrl;

    /**
     * 行驶证副本图片
     */
    private Long adriverLicenseCopy;

    /**
     * 行驶证副本图片url
     */
    private String adriverLicenseCopyUrl;

    /**
     * 行驶证编号
     */
    private String drivingLicenseSn;

    /**
     * 行驶证副本(图片保存编号)
     */
    private Long adrivingLicenseCopy;

    /**
     * 操作人id
     */
    private Long updateOpId;

    /**
     * 副驾驶ID
     */
    private Long copilotDriverId;

    /**
     * 随车司机ID
     */
    private Long followDriverId;

    /**
     * 0不可用 1可用 9被移除
     */
    private Integer verState;

    /**
     * 最后运作订单ID
     */
    private Long orderId;

    /**
     * 最后运作订单时间
     */
    private LocalDateTime lastOrderDate;

    /**
     * 是否审批成功 1-是
     */
    private Integer isAuthSucc;

    /**
     * 运营证图片ID(运输证)
     */
    private Long operCertiId;

    /**
     * 车辆租赁协议
     */
    private Long rentAgreementId;

    /**
     * 车辆租赁协议url
     */
    private String rentAgreementUrl;

    /**
     * 行驶证上所有人
     */
    private String drivingLicenseOwner;

    /**
     * 挂靠人手机号
     */
    private String attachUserMobile;

    /**
     * 挂靠人名称
     */
    private String attachUserName;

    /**
     * 挂靠人用户编号
     */
    private Long attachUserId;

    /**
     * 快速创建标志（0、非快速创建，1、快速创建）
     */
    private Integer quickFlag;

    /**
     * 特殊运营证文件编号
     */
    private Long specialOperCertFileId;

    /**
     * 特殊运营证文件URL
     */
    private String specialOperCertFileUrl;


    /**
     * 车辆型号
     */
    private String vehicleModel;

    /**
     *0 闲置 1禁用
     */
    private Short idle;
}
