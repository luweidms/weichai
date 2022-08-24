package com.youming.youche.system.domain.mycenter;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName BankAccountInfo
 * @Description 帐户银行卡绑定表
 * @Author zag
 * @Date 2022/2/15 16:35
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
     * 用户编号
     */
    private Long userId;

    /**
     * 银行编号
     */
    private String bankId;

    /**
     * 银行名称（简称）
     */
    private String bankName;

    /**
     * 账户名称
     */
    private String acctName;

    /**
     * 账户编号
     */
    private String acctNo;

    /**
     * 支行名称
     */
    private String branchName;

    /**
     * 支行代码
     */
    private String branchId;

    /**
     * 状态时间
     */
    private LocalDateTime stsDate;

    /**
     * 状态 1、在用 0、失效
     */
    private Integer sts;

    /**
     * 手机
     */
    private String billid;

    /**
     * 省份ID
     */
    private Long provinceid;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 城市ID
     */
    private Long cityid;

    /**
     * 城市
     */
    private String cityName;

    /**
     * 县区ID
     */
    private Long districtid;

    /**
     * 县区
     */
    private String districtName;

    /**
     * 身份证号
     */
    private String identification;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    private LocalDateTime updateDate;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 银行类型：0、私人账户，1网商对公账户,2微信账户,3路歌对公账户,4收款对公账户
     */
    private Integer bankType;

    /**
     * 二维码图片ID
     */
    private Long weqrCodeId;

    /**
     * 二维码图片URL
     */
    private String weqrCodeUrl;

    /**
     * 微信账号
     */
    private String wechatAccount;

    /**
     * 平安收款账户编号--商户
     */
    private String pinganCollectAcctId;

    /**
     * 平安付款账户编号--普通
     */
    private String pinganPayAcctId;

    /**
     * 对应平安的ThirdCustId
     */
    private String pinganMoutId;

    /**
     * 对应平安的ThirdCustId
     */
    private String pinganNoutId;

    /**
     * 是否默认账户 0 否 1 是
     */
    private Integer isDefaultAcct;

    /**
     * 是否收款账户（0或空： 否；1：是）
     */
    private Integer isCollectAmount;

    /**
     * 绑定该卡的角色类型
     */
    private Integer bindUserType;

    private String businessLicenseNo;
}
