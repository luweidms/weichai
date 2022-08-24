package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 车队表
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysTenantDef extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 租户CODE
     */
    private String tenantCode;

    /**
     * 租户名称(车队全称)
     */
    private String name;

    /**
     * 车队简称
     */
    private String shortName;

    /**
     * 省
     */
    private Integer provinceId;

    private String provinceName;

    /**
     * 市
     */
    private Integer cityId;

    private String cityName;

    /**
     * 县/区
     */
    private Integer districtId;

    private String districtName;

    /**
     * 常用办公地址
     */
    private String address;

    /**
     * 省
     */
    private Integer companyProvinceId;

    private String companyProvinceName;

    /**
     * 市
     */
    private Integer companyCityId;

    private String companyCityName;

    /**
     * 县/区
     */
    private Integer companyDistrictId;

    private String companyDistrictName;

    /**
     * 企业注册住所
     */
    private String companyAddress;

    private String logo;

    /**
     * 租户联系人
     */
    private String linkMan;

    /**
     * 租户联系人电话
     */
    private String linkPhone;

    /**
     * 租户联系人邮箱
     */
    private String linkEmail;

    /**
     * 租户域名配置
     */
    private String domain;

    /**
     * 租户皮肤配置
     */
    private String style;

    private Long adminUser;

    /**
     * 实际控制人
     */
    private String actualController;

    /**
     * 实际控制人联系电话
     */
    private String actualControllerPhone;

    /**
     * 实际控制人身份证号码
     */
    private String identification;

    /**
     * 实际控制人身份证照片
     */
    private String identificationPicture;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 营业执照号码
     */
    private String businessLicenseNo;

    /**
     * 企业资质
     */
    private String companyQualifications;

    /**
     * 冻结状态1、未冻结2、已冻结
     */
    private Integer frozenState;

    /**
     * 车队类型：1、车队；2、三方；3、混合
     */
    private Integer tenantType;

    /**
     * 员工数
     */
    private Integer staffNumber;

    /**
     * 自有车数量
     */
    private Integer vehicleNumber;

    /**
     * 年营业额
     */
    private Long annualTurnover;

    /**
     * 状态 0 禁用 1 启用
     */
    private Integer state;

    /**
     * 启用禁用原因
     */
    private String stateReason;

    /**
     * 启用禁用操作人名字
     */
    private String stateOperatorName;

    /**
     * 售前跟踪ID
     */
    private Long preSaleServiceId;

    /**
     * 售前跟踪员工姓名
     */
    private String preSaleServiceName;

    /**
     * 售后跟踪ID
     */
    private Long afterSaleServiceId;

    /**
     * 售后跟踪员工姓名
     */
    private String afterSaleServiceName;

    /**
     * 小车队状态（0、不是小车队；1、新建的小车队；2、申请升级；3、审核不通过；4、审核通过）
     */
    private Integer virtualState;

    /**
     * 平台服务费
     */
    private Long serviceFee;

    /**
     * 支付平台服务费时间
     */
    private String payServiceFeeDate;

    /**
     * 易流公钥
     */
    private String ylPublicKey;

    /**
     * 易流私钥
     */
    private String ylPrivateKey;

    /**
     * G7机构代码
     */
    private String g7Code;

    /**
     * 客户满意度评分
     */
    private Double avgScore;

    /**
     * 撤场日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveDate;

    /**
     * 入场日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime entranceDate;

    /**
     * 缴费日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payDate;

    /**
     * 签约日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signDate;

    /**
     * 售后责任组
     */
    private Long afterSaleOrgId;

    /**
     * 售前责任组
     */
    private Long preSaleOrgId;

    /**
     * 首次缴费时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstPayDate;

    /**
     * 交付周期（天）
     */
    private Integer deliveryCycle;

    /**
     * 预约入场日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointEntranceDate;

    /**
     * 付款状态（0、未到期；1、付款中；2、付款成功；3、已逾期）
     */
    private Integer payState;

    /**
     * 合同编码
     */
    private String contractCode;

    /**
     * 售前服务电话
     */
    @TableField(exist = false)
    private String preSaleServicePhone;

    /**
     * 售后服务电话
     */
    @TableField(exist = false)
    private String afterSaleServicePhone;

    /**
     * 超管身份证号码
     */
    @TableField(exist = false)
    private String linkManIdentification;

    /**
     * 自由车数量
     */
    @TableField(exist = false)
    private Integer ownCar;

    /**
     * 外调车数量
     */
    @TableField(exist = false)
    private Integer otherCar;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String stateName;

    /**
     * 是否绑卡
     */
    @TableField(exist = false)
    private Integer bindState;

    @TableField(exist = false)
    private String bindCardStr;

    public Integer getVehicleNumber() {
        if (this.ownCar == null) {
            return 0;
        } else {
            return this.ownCar;
        }
    }

    public Integer getOtherCar() {
        if (this.otherCar == null) {
            return 0;
        } else {
            return this.otherCar;
        }
    }


    public String getVirtualStateName() {
        if (this.virtualState == 0) {
            return "正式车队";
        } else {
            return "代收车队";
        }
    }

    public String getStateName() {
        if (this.state == null || this.state == 1) {
            return "启用";
        } else {
            return "禁用";
        }
    }

    public String getCreateTimeSter() {
        if (this.getCreateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return getCreateTime().format(formatter);
        } else {
            return "";
        }
    }





}
