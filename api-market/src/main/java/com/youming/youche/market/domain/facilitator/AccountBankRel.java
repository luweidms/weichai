package com.youming.youche.market.domain.facilitator;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AccountBankRel extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账户编号
     */
    private Long acctId;
    /**
     * 账户名称
     */
    private String acctName;
    /**
     * 账户编号
     */
    private String acctNo;
    /**
     * 银行编号
     */
    private String bankId;
    /**
     * 银行名称（简称）
     */
    private String bankName;
    /**
     * 银行类型：0、私人账户，1网商对公账户,2微信账户,3路歌对公账户,4收款对公账户
     */
    private Integer bankType;
    /**
     * 手机
     */
    private String billid;
    /**
     * 绑定该卡的角色类型
     */
    private Integer bindUserType;
    /**
     * 支行代码
     */
    private String branchId;
    /**
     * 支行名称
     */
    private String branchName;
    /**
     * 营业执照编号
     */
    private String businessLicenseNo;
    /**
     * 城市
     */
    private String cityName;
    /**
     * 城市ID
     */
    private Long cityid;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 县区
     */
    private String districtName;
    /**
     * 县区id
     */
    private Long districtid;
    /**
     * 身份证号
     */
    private String identification;
    /**
     * 是否收款账户（0或空： 否；1：是）
     */
    private Integer isCollectAmount;
    /**
     * 是否默认账户 0 否  1 是
     */
    private Integer isDefaultAcct;
    /**
     * 操作员id
     */
    private Long opId;
    /**
     * 平安收款账户编号--商户/招行商户编号
     */
    private String pinganCollectAcctId;
    /**
     * 对应平安的ThirdCustId
     */
    private String pinganMoutId;
    /**
     * 对应平安的ThirdCustId
     */
    private String pinganNoutId;
    /**
     * 平安付款账户编号--普通/招行商户子编号
     */
    private String pinganPayAcctId;
    /**
     * 省份
     */
    private String provinceName;
    /**
     * 省份id
     */
    private Long provinceid;
    /**
     * 状态 1、在用  0、失效
     */
    private Integer sts;
    /**
     * 状态时间
     */
    private LocalDateTime stsDate;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 修改时间
     */
    private LocalDateTime updateDate;
    /**
     * 修改操作员id
     */
    private Long updateOpId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 微信账号
     */
    private String wechatAccount;
    /**
     * 二维码图片ID
     */
    private Long weqrCodeId;
    /**
     * 二维码图片URL
     */
    private String weqrCodeUrl;


}
