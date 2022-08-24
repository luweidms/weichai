package com.youming.youche.market.vo.maintenanceaudit;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AccountBankByTenantIdVO implements Serializable {


    private static final long serialVersionUID = 6738374731712770542L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 银行卡信息表的主键
     */
    private Long bankRelId;

    /**
     * 银行卡号
     */
    private String acctNo;

    /**
     * 操作类型：1 表示可以修改 2 表示可以查看
     */
    private Integer opType;

    /**
     * 账户类型：0 个人 1 对公
     */
    private Integer bankType;

    /**
     * 用户类型：1 员工 2 服务商 3 司机 6 超管 7 收款人
     */
    private Integer userType;

    /**
     * 是否默认账户 0 否  1 是
     */
    private Integer isDefault;

    /**
     * 账户编号
     */
    private Long acctId;

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


}

