package com.youming.youche.system.dto;

import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.domain.tenant.SysTenantBusinessState;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zengwen
 * @date 2022/4/24 13:05
 */
@Data
public class SysTenantOutDto implements Serializable {

    /**
     * 小车队状态
     */
    private Integer virtualState;

    /**
     * 当前开票平台名称，页面报错需要
     */
    private String plateName;

    private String preSaleServicePhone;

    private String afterSaleServicePhone;

    /**
     * sys_tenant_register表的主键，在审核创建车队时，需要带回此参数
     */
    private Long registerId;
    /**
     * sys_tenant_def表的主键，在修改车队档案时需要带回次参数
     */
    private Long tenantId;

    /** 手机号码 */
    private String linkPhone;
    /**
     * 超管姓名
     */
    private String linkMan;
    /**
     * 超管身份证号码
     */
    private String linkManIdentification;
    /** 企业名称 */
    private String companyName;
    /** 车队简称 */
    private String shortName;

    /** 企业注册住所 */
    private String companyAddress;
    private Integer companyProvinceId;
    private String companyProvinceName;
    private Integer companyCityId;
    private String companyCityName;
    private Integer companyDistrictId;
    private String companyDistrictName;

    /** 常用办公地址 */
    private String address;
    //省市区
    private Integer provinceId;
    private String provinceName;
    private Integer cityId;
    private String cityName;
    private Integer districtId;
    private String districtName;
    /** 企业营业执照 */
    private String businessLicense;
    /** 企业资质 */
    private String companyQualifications;

    /** 企业营业执照号(统一社会信用代码) */
    private String businessLicenseNo;
    private String logo;
    /** 实际控制人 */
    private String actualController;
    private String actualControllerPhone;
    /** 身份证号码 */
    private String identification;
    /** 身份证号码图片 */
    private String identificationPicture;

    private Integer deliveryCycle;
    private Date appointEntranceDate;
//    /**
//     * 车队类型
//     */
//    private Integer tenantType;
//    /** 员工数 */
//    private Integer staffNumber;
//    /** 自有车数量 */
//    private Integer vehicleNumber;
//    /** 年营业额 */
//    private Long annualTurnover;
//    private Double annualTurnoverDouble;

    /** 售前跟踪  2018-6-14*/
    private Long preSaleServiceId;
    private String preSaleServiceName;
    /** 售后跟踪*/
    private Long afterSaleServiceId;
    private String afterSaleServiceName;
    private Long preSaleOrgId;
    private String preSaleOrgName;
    private Long afterSaleOrgId;
    private String afterSaleOrgName;
    /** 签约日期 */
    private Date signDate;
    /** 缴费日期 */
    private Date payDate;
    /** 入场日期 */
    private Date entranceDate;
    /** 撤场日期 */
    private Date leaveDate;
    /** 满意度评分 */
    private Double avgScore;

    /** 票据信息 */
    private BillSetting billSetting;

    private Long adminUser;

    private String ylPublicKey;
    private String ylPrivateKey;
    private String g7Code;

    private String contractCode;

    /**
     * 经营信息
     */
    private SysTenantBusinessState businessState;

    //超管用户名
    public String adminUserName;

    private Long serviceFee;//平台服务费
    private String payServiceFeeDate;//服务费缴纳日期

}
