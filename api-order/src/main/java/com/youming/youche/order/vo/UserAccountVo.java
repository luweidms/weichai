package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: luona
 * @date: 2022/4/7
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class UserAccountVo implements Serializable {
    private long accId;
    private long balance;
    /**
     * ETC金额
     */
    private long etcBalance;
    private long etcBalanceD;
    private double etcBalanceDD;
    private double balanceD;
    /**
     * 油卡抵押金额
     */
    private double pledgeOilcardFeeD;
    /**
     * 应收逾期
     */
    private double receivableOverdueBalanceD;
    private double payableOverdueBalanceD;
    private Long sourceTenantId;
    private Long fromTenantId;
    /**
     * 应收逾期
     */
    private Long receivableOverdueBalance;
    private Long payableOverdueBalance;
    private String userName;
    /**
     * 账户类型 1母卡 2子卡
     */
    private String accountTypeName;
    /**
     * 维修基金
     */
    private Long repairFund;
    private double repairFundD;
    /**
     * 油卡抵押金额
     */
    private Long pledgeOilCardFee;
    /**
     * 账户状态
     */
    private Integer accState;
    private String shortName;
    /**
     *资金渠道
     */
    private String vehicleAffiliation;
    /**
     * 账户状态
     */
    private String accStateName;
    private long goldBalance;
    private long integraBalance;
    private Integer accLevel;
    private String accPassword;
    private Date modPwdtime;
    /**
     * 加油使用未到期金额(分)
     */
    private long marginBalance;
    private Long oilBalance;
    private Long beforePay;
    private double marginBalanceD;
    /**
     * 油账户明细 返回 值
     */
    private double oilBalanceD;
    private double beforePayD;
    private String linkman;
    private String companyAddress;
    private long postalCode;
    private String companyWebsite;
    private String email;
    private String contactNumber;
    private String mobilePhone;
    private String companyFax;
    /**
     * 公司名称
     */
    private String companyName;
    private String legalPerson;
    private String busiLiceNumber;
    private Long qualificationDoc;
    private Date foundingTime;
    private String companyProfile;
    private Long companyPicture;
    private String identification;
    private String loginName;
    private Long userPrice;
    private Long idenPicture;
    private String stateReason;
    private Long orgId;
    private Long rootOrgId;
    private String userPriceUrl;
    private String idenPictureUrl;
    private String busiLiceNumberUrl;
    private String companyPictureUrl;
    private String qualificationDocUrl;
    private Long drivingLicense;
    private Long adriverLicense;
    private String drivingLicenseUrl;
    private String adriverLicenseUrl;
    private String stateName;
    private String userTypeALias;
    //	//private Long opId;
    private Date opDate;
    private Integer idType;
    private String userPriceUrlLoaction;
    private String inviteBillId;
    private Integer isPerfectInfo;
    private Long qrCodeId;
    private String qrCodeUrl;
    private String managerRemark;
    private Long qcCerti;
    private String qcCertiUrl;
    private String businessBillId;
    private String employeeNumber;
    private String staffPosition;
    private int sourceFlag;
    /**
     * 公司名称
     */
    private String companyNameShort;
    private Integer authImportant;
    private String authImportantReason;
    private Long authManId;
    private long operatorId;
    private String operatorName;
    private String loginAcct;
    private long userId;
    private String billId;
    /**
     * 用户类型：1-主驾驶 2-副驾驶 3- 经停驾驶 4-车队
     */
    private Integer userType;
    private String password;
    private Integer tryTimes;
    private Integer lockFlag;
    private Integer state;
    //private Date createDate;
    /**
     * 备注
     */
    private String remark;
    //private Long opId;
    private String tenantCode;
    /**
     * 用户状态
     */
    private Integer zhUserState;
    /**
     * 用户类型：1-主驾驶 2-副驾驶 3- 经停驾驶 4-车队
     */
    private String userTypeName;
    private String rayableOverdueBalanceD;
}
