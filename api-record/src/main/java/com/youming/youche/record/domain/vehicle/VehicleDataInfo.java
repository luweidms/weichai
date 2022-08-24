package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class VehicleDataInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 车辆编号
     */
   // @TableId(value = "vehicle_code", type = IdType.AUTO)
//    private Long vehicleCode;

    /**
     * 用户ID/发布人用户编号(司机归属用户ID)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long userId;

    /**
     * 车牌号码
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vehicleAddress;

    /**
     * 车辆功能
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer vehicleType;

    /**
     * 车辆品牌
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String brandModel;

    /**
     * 货箱结构
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.INTEGER)
    private Integer vehicleStatus;

    /**
     * 随车手机
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String contactNumber;

    /**
     * 司机名称
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String linkman;

    /**
     * 身份证
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String identifcationCard;

    /**
     * 设备编码
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String equipmentCode;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 操作员id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long opId;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime opDate;

    /**
     * 证件类型 1、表示身份证 2、表示驾驶证
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer idType;

    /**
     * 设备卡号码
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String equipmentCardnb;

    /**
     * 设备授权号码
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String equipmentEmpowrnb;

    /**
     * 设备设置IP指令
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String equipmentSetcommand;

    /**
     * 设备名称
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String equipmentName;

    /**
     * 车辆图片
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long vehiclePicture;

    /**
     * 定位服务:0-无；1-G7；2-app定位; 3-易流
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer locationServ;

    /**
     * 拖挂及轮轴
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer vehicleTariler;

    /**
     * 行驶证
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String drivingLicense;

    /**
     * 驾驶证
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long adrivingLicense;

    /**
     * 运营证
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String operCerti;

    /**
     * 常跑路线
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String oftenRun;

    /**
     * 常跑省份
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String oftenRpovince;

    /**
     * 常跑地市
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String oftenRegion;

    /**
     * 常跑县区
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String oftenCountry;

    /**
     * 轻货方数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.VARCHAR)
    private String lightGoodsSquare;

    /**
     * 身份证图片id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long idenPicture;

    /**
     * 车辆图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vehiclePicUrl;

    /**
     * 身份证图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String identifcationCardUrl;

    /**
     * 行驶证图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String drivingLicenseUrl;

    /**
     * 驾驶证图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String adriverLicenseUrl;

    /**
     * 运营证图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String operCertiUrl;

    /**
     * 标识数据来源，0、表示平台发展1、表示爬虫2、表示百度3、表示自动生成4、表示外购
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceFlag;

    /**
     * 司机用户编号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long driverUserId;

    /**
     * 租户id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tenantId;

    /**
     * 车队名称
     */
    @TableField(exist = false)
    private String tenantName;

    /**
     * 车架号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vinNo;

    /**
     * 油耗
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String oilWear;

    /**
     * 车主
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String carOwner;

    /**
     * 车主手机
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String carPhone;

    /**
     * 运营范围
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String carRemark;

    /**
     * 挂靠或者托管（1：挂靠，2：托管）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer affilateCustodian;

    /**
     * etc卡号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String etcCardNumber;

    /**
     * etc金额(单位分)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long etcAmount;

    /**
     * 牌照类型(1:整车，2：拖头)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer licenceType;

    /**
     * 车辆隶属关系
     */
    @TableField("vehicle_Affiliation")
    private String vehicleAffiliation;

    /**
     * 发动机号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String engineNo;

    /**
     * 申请状态 0未申请 1申请加入托管 2申请取消托管
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer custodialPendingsStatus;

    /**
     * 版本
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String carVersion;

    /**
     * 行驶证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate vehicleValidityTime;

    /**
     * 营运证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate operateValidityTime;

    /**
     * 行驶证副本图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String adriverLicenseCopyUrl;

    /**
     * 行驶证编号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String drivingLicenseSn;

    /**
     * 行驶证副本(图片保存编号)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long adriverLicenseCopy;

    /**
     * 副驾驶ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long copilotDriverId;

    /**
     * 随车司机ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long followDriverId;

    /**
     * 认证状态：1-未认证 2-已认证 3-认证失败
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer authState;

    /**
     * 认证内容
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String auditContent;

    /**
     * 最后运作订单ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long orderId;

    /**
     * 最后运作订单时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOrderDate;

    /**
     * 是否显示审核按钮 0否 1是
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer isAuth;

    /**
     * 运营证图片ID(运输证)
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.BIGINT)
    private Long operCertiId;

    /**
     * 车辆租赁协议
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.BIGINT)
    private Long rentAgreementId;

    /**
     * 车辆租赁协议url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String rentAgreementUrl;

    /**
     * 行驶证上所有人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String drivingLicenseOwner;

    /**
     * 挂靠人手机号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String attachUserMobile;

    /**
     * 挂靠人名称
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String attachUserName;

    /**
     * 挂靠人用户编号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long attachUserId;

    /**
     * 资料完整性
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String completeness;

    /**
     * 快速创建标志（0、非快速创建，1、快速创建）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer quickFlag;

    /**
     * 特殊运营证文件编号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.BIGINT)
    private Long specialOperCertFileId;

    /**
     * 特殊运营证文件URL
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String specialOperCertFileUrl;

    /**
     * 中交北斗入网状态（空、待检查；1、未入网；2、已入网）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer netState;

    /**
     * 维保里程
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer maintenanceDis;

    /**
     * 最后维保时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMaintenanceDate;

    /**
     * 车长
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vehicleLengthName;

    /**
     * 货箱结构
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vehicleStatusName;

    /**
     * 车辆型号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vehicleModel;

    /**
     * 0 闲置 1禁用
     */
    private Short idle;


}
