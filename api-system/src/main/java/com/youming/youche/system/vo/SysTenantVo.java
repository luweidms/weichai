package com.youming.youche.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.domain.tenant.SysTenantBusinessState;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luwei
 * @version 1.0
 * @description: 新增车队入参
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

	/**
	 * 超过用户id
	 */
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

	/**
	 * 公司省份id
	 */
	private Integer companyProvinceId;

	/**
	 * 公司省份名称
	 */
	private String companyProvinceName;

	/**
	 * 公司城市id
	 */
	private Integer companyCityId;

	/**
	 * 公司城市名称
	 */
	private String companyCityName;

	/**
	 * 公司区域id
	 */
	private Integer companyDistrictId;

	/**
	 * 公司区域名称
	 */
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

	/**
	 * 城市id
	 */
	private Integer cityId;

	/**
	 * 城市名称
	 */
	private String cityName;

	/**
	 * 区域id
	 */
	private Integer districtId;

	/**
	 * 区域名称
	 */
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

	/**
	 * 车队头像地址
	 */
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

	/**
	 * 售后人名称
	 */
	private String afterSaleServiceName;

	/**
	 * 售前部门id
	 */
	private Long preSaleOrgId;

	/**
	 * 售前部门名称
	 */
	private String preSaleOrgName;

	/**
	 * 售后部门id
	 */
	private Long afterSaleOrgId;

	/**
	 * 售后部门名称
	 */
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

	/**
	 * 合同编码
	 */
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
