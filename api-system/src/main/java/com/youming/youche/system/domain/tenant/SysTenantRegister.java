package com.youming.youche.system.domain.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 车队注册表，用于收集车队信息
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysTenantRegister extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 租户联系人（预留字段）
     */
    private String linkMan;

    /**
     * 租户联系人手机号码，作为登录系统的账号
     */
    private String linkPhone;

    /**
     * 租户联系人邮箱（预留字段）
     */
    private String linkEmail;

    /**
     * 企业名称
     */
    private String companyName;

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

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 营业执照号码
     */
    private String businessLicenseNo;

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
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 状态;默认0(不可用),1(可用)
     */
    private Integer state;

    /**
     * 创建车队状态：1、未创建，2、创建中，3、已创建
     */
    private Integer buildState;

    /**
     * 审核状态：1-待审核2-审核通过3-审核不通过
     */
    private Integer auditState;

    /**
     * 审核原因
     */
    private String auditContent;

    /**
     * 审核人名字
     */
    private String auditContentName;


}
