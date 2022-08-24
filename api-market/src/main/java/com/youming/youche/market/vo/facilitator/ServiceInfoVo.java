package com.youming.youche.market.vo.facilitator;

import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.annotation.SysStaticDataInfoDict;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import lombok.Data;

import java.io.Serializable;

import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_CITY;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_DISTRICT;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_PROVINCE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_STATE_DESC;

@Data
public class ServiceInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户编码
     */
    private Long serviceUserId;
    /**
     * 账号（手机号
     */
    private String loginAcct;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    @SysStaticDataInfoDict(dictDataSource = "SYS_SERVICE_BUSI_TYPE")
    private Integer serviceType;
    /**
     * 服务商类型
     */
    private String serviceTypeName;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 地址
     */
    private String companyAddress;
    /**
     * 负责人
     */
    private String linkman;
    /**
     * 用户头像
     */
    private Long userPrice;
    /**
     * 用户头像路径
     */
    private String userPriceUrl;
    /**
     * 回单图片
     */
    private String receiptCode;
    /**
     * 回单图片ID 路徑
     */
    private String receiptCodeUrl;
    /**
     * 身份证背面
     */
    private Long idenPictureBack;
    /**
     * 身份证图片正面
     */
    private Long idenPictureFront;
    /**
     * 身份证图片正面路径
     */
    private String idenPictureBackUrl;
    /**
     * 身份证正面路径
     */
    private String idenPictureFrontUrl;
    /**
     * 收据登录账户
     */
    private String receiptLoginAcct;
    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;
    /**
     * 身份证号码
     */
    private String identification;

    /**
     * 账户名称
     */
    private String puAcctName;

    /**
     * 账号号
     */
    private String puAcctNo;

    /**
     * 账户银行
     */
    @SysStaticDataInfoDict(dictDataSource = "BANK_TYPE")
    private String puBankId;

    /**
     * 账户银行名称
     */
    private String puBankName;

    /**
     * 账户支行名称
     */
    private String puBranchName;

    /**
     * 账户支行ID
     */
    private String puBranchId;

    /**
     * 所在省
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_PROVINCE)
    private Long puProvinceId;

    /**
     * 所在省名称
     */
    private String puProvinceIdName;

    /**
     * 所在城市
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_CITY)
    private Long puCityId;

    /**
     * 所在城市名称
     */
    private String puCityIdName;

    /**
     * 所在市县
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_DISTRICT)
    private Long puDistrictId;

    /**
     * 所在市县名称
     */
    private String puDistrictIdName;

    /**
     * 关系ID
     */
    private Long puRelSeq;

    /**
     * 私户账户名称
     */
    private String pvAcctName;

    /**
     * 私户账户号
     */
    private String pvAcctNo;

    /**
     * 私户银行
     */
    @SysStaticDataInfoDict(dictDataSource = "BANK_TYPE")
    private String pvBankId;

    /**
     * 私户银行名称
     */
    private String pvBankName;

    /**
     * 私户支行名称
     */
    private String pvBranchName;

    /**
     * 私户支行
     */
    private String pvBranchId;

    /**
     * 私户所在省
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_PROVINCE)
    private Long pvProvinceId;

    /**
     * 私户所在省名称
     */
    private String pvProvinceIdName;

    /**
     * 私户所在城市
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_CITY)
    private Long pvCityId;

    /**
     * 私户所在城市名称
     */
    private String pvCityIdName;

    /**
     * 私户所在县市
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_DISTRICT)
    private Long pvDistrictId;

    /**
     * 私户所在县市名称
     */
    private String pvDistrictIdName;

    /**
     * 私户关系Id
     */
    private Long pvRelSeq;
    /**
     * 是否开票（1.是、2.否）
     */
    private Integer isBill;
    /**
     * 是否开票
     */
    private String isBillName;
    /**
     * 账期
     */
    private Integer paymentDays;
    /**
     * 关系主键
     */
    private Long relId;
    /**
     * 状态
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_STATE_DESC)
    private Integer state;
    /**
     * 状态
     */
    private String stateName;
    /**
     * '是否有开票能力（1.有，2.无）',
     */
    private String isBillAbilityName;


    /**
     * '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    @SysStaticDataInfoDict(dictDataSource = EnumConsts.SysStaticData.CUSTOMER_AUTH_STATE)
    private Integer authState;
    /**
     * 服务商审核状态 1.未认证 2.已认证 3.认证失败
     */
    private String authStateName;
    /**
     * 结算方式，1账期，2月结
     */
    private Integer balanceType;
    /**
     * 结算方式
     */
    private String balanceTypeName;
    /**
     * 账期结算月份
     */
    private Integer paymentMonth;

    /**
     * 授信金额
     */
    private Long quotaAmt;

    /**
     * 授信金额
     */
    private String quotaAmtStr;

    /**
     * 已使用授信金额
     */
    private Long useQuotaAmt;

    /**
     * 已使用授信金额
     */
    private String useQuotaAmtStr;

    /**
     * 是否代收
     */
    private Integer agentCollection;

    /**
     * 车队服务商关系审核信息
     */
    private TenantServiceRelVer tenantServiceRelVer;

    /**
     * 车队服务商关系信息
     */
    private TenantServiceRel tenantServiceRel;
}
