package com.youming.youche.capital.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.capital.domain.tenant.BillSetting;
import com.youming.youche.capital.domain.tenant.SysTenantBusinessState;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luwei
 * @version 1.0
 * @description: TODO
 * @date 2022/1/6 2:33 下午
 */
@Data
@Accessors(chain = true)
public class SysTenantVo implements Serializable {

	/**
	 * sys_tenant_def表的主键，在修改车队档案时需要带回次参数
	 */
	private Long registerId;

	/**
	 * sys_tenant_def表的主键，在修改车队档案时需要带回次参数
	 */
	private Long tenantId;

	/**
	 * 手机号码
	 */
	private String linkPhone;

	/**
	 * 超管姓名
	 */
	private String linkMan;

	/**
	 * 超管身份证号码
	 */
	private String linkManIdentification;

	private Long adminUser;

	/**
	 * 企业名称
	 */
	private String companyName;

	/**
	 * 车队简称
	 */
	private String shortName;

	/**
	 * 企业注册住所
	 */
	private String companyAddress;

	private Integer companyProvinceId;

	private String companyProvinceName;

	private Integer companyCityId;

	private String companyCityName;

	private Integer companyDistrictId;

	private String companyDistrictName;

	/**
	 * 常用办公地址
	 */
	private String address;

	// 省市区
	private Integer provinceId;

	/**
	 * 省名称
	 */
	private String provinceName;

	private Integer cityId;

	private String cityName;

	private Integer districtId;

	private String districtName;

	/**
	 * 企业营业执照
	 */
	private String businessLicense;

	/**
	 * 企业资质
	 */
	private String companyQualifications;

	/**
	 * 企业营业执照号(统一社会信用代码)
	 */
	private String businessLicenseNo;

	private String logo;

	/**
	 * 实际控制人
	 */
	private String actualController;

	/**
	 * 实际控制人联系电话
	 */
	private String actualControllerPhone;

	/**
	 * 身份证号码
	 */
	private String identification;

	/**
	 * 身份证号码图片
	 */
	private String identificationPicture;

	/**
	 * 交付周期（天）
	 */
	private Integer deliveryCycle;

	/**
	 * 预约入场日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date appointEntranceDate;

	/**
	 * 售前跟踪 2018-6-14
	 */
	private Long preSaleServiceId;

	/**
	 * 售前跟踪员工姓名
	 */
	private String preSaleServiceName;

	/**
	 * 售后跟踪
	 */
	private Long afterSaleServiceId;

	private String afterSaleServiceName;

	private Long preSaleOrgId;

	private String preSaleOrgName;

	private Long afterSaleOrgId;

	private String afterSaleOrgName;

	/**
	 * 签约日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date signDate;

	/**
	 * 缴费日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date payDate;

	/**
	 * 入场日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date entranceDate;

	/**
	 * 撤场日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date leaveDate;

	/**
	 * 满意度评分
	 */
	private Double avgScore;

	private String ylPublicKey;

	private String ylPrivateKey;

	private String g7Code;

	private String contractCode;

	/**
	 * 系统服务费
	 */
	private Long serviceFee;

	/**
	 * 经营信息
	 */
	private SysTenantBusinessState businessState;

	/** 票据信息 */
	private BillSetting billSetting;

	/**
	 * 售前服务电话
	 */
	private String preSaleServicePhone;

	/**
	 * 售后服务电话
	 */
	private String afterSaleServicePhone;

	/**
	 * 小车队状态
	 * @see SysStaticDataEnum.VIRTUAL_TENANT_STATE
	 */
	private Integer virtualState;
	/**
	 *	当前开票平台名称，页面报错需要
	 */
	private String plateName;

	//超管用户名
	public String adminUserName;

	/**
	 *  服务费缴纳日期
	 */
	private String payServiceFeeDate;



}
