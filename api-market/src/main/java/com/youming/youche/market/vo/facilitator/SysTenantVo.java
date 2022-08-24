package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.domain.facilitator.BillSetting;
import lombok.Data;

import java.util.Date;
@Data
public class SysTenantVo {
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

    /**
     * 公司省份ID
     */
    private Integer companyProvinceId;

    /**
     * 公司省份名称
     */
    private String companyProvinceName;

    /**
     * 公司城市ID
     */
    private Integer companyCityId;

    /**
     * 公司城市名称
     */
    private String companyCityName;

    /**
     * 公司区号ID
     */
    private Integer companyDistrictId;

    /**
     * 公司区号名称
     */
    private String companyDistrictName;

    /** 常用办公地址 */
    private String address;
    //省市区
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 城市ID
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区ID
     */
    private Integer districtId;

    /**
     * 区名称
     */
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

    /**
     * 交货周期
     */
    private Integer deliveryCycle;

    /**
     * 预约入场日期
     */
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

    /**
     * 售前跟踪员工名称
     */
    private String preSaleServiceName;
    /** 售后跟踪*/
    private Long afterSaleServiceId;

    /**
     * 售后跟踪员工名称
     */
    private String afterSaleServiceName;

    /**
     * 售前责任组
     */
    private Long preSaleOrgId;

    /**
     * 售前责任组名称
     */
    private String preSaleOrgName;

    /**
     * 售后责任组
     */
    private Long afterSaleOrgId;

    /**
     * 售后责任组名称
     */
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

    /**
     * 管理员信息ID
     */
    private Long adminUser;

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
     * 合同编码
     */
    private String contractCode;

    /**
     * 经营信息
     */
//    private BusinessState businessState;
}
