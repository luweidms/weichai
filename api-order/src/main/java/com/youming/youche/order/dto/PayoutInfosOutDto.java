package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author: wuhao
 * @date: 2022/5/12
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class PayoutInfosOutDto implements Serializable {
    private Date createDate;
    private Long orderId;
    private Long txnAmt;
    private double txnAmtDouble;
    private Long billServiceFee;//票据服务费（路哥）
    private double billServiceFeeDouble;
    private Long appendFreight;//附加运费（56K）
    private double appendFreightDouble;
    private Long sumTxnFee;//（总金额=业务金额+票据服务费+附加运费）
    private double sumTxnFeeDouble;
    private Long linkPhone;
    private Long busiId;
    private String busiIdName;
    private String respCode;
    private Integer payConfirm;
    private String name;
    private String stateName;
    private String respCodeName;
    /**
     * 流水号
     */
    private Long flowId;
    private Integer verificationState;
    private Long sourceRegion;
    private Long desRegion;
    private String sourceRegionName;
    private String desRegionName;
    private Long payTenantId;
    private String payTenantName;
    private Long userId;
    private Long payObjId;
    private Integer userType;
    private Integer isAutoMatic;
    private Long objId;
    private Long isNeedBill;
    /**
     * 票据类型
     */
    private String isNeedBillName;
    private Date updateDate;
    /**
     * 账户类型：0 个人 1 对公
     */
    private Integer bankType;
    private String accName;
    /**
     * 账号
     */
    private String accNo;
    private Long tenantId;
    private String payConfirmName;
    /**
     * 账户类型：0 个人 1 对公
     */
    private String bankTypeName;
    private String isAutomaticName;
    private Long noVerificatMoney;
    private Long verificatMoney;
    private Integer isDriver;
    private Integer viewType;
    /**
     * 账户类型 1母卡 2子卡
     */
    private Integer accountType;
    private String payBankAccName;
    private String remark;
    private Integer isTurnAutomatic;
    private Date verificationDate;
    private Long subjectsId;
    private String subjectsIdName;
    private Long oilAffliation;
    private String userName;
    private String busiCode;
    private String plateNumber;
    private Integer orderType;
    private String payBankAccNo;
    private Date payTime;
    private String respMsg;
    private String receivablesBankAccName;
    private String receivablesBankAccNo;
    /**
     *资金渠道
     */
    private Long vehicleAffiliation;
    private String completeTime;
    private String acctNo;
    private String payAccNo;
    private String accNameAndAccNo;
    private String respCodeName100;
    private LocalDateTime payTime100;
    private Integer virtualState;
    private Boolean hasPermission;//是否有审核权限
    private Boolean isNeedPassword;//是否需要密码
    private Boolean isFinallyNode;//是否是最后一个节点
    private String pinganCollectAcctId;//最终收款账户虚拟卡号
    private Long fileId;//图片Id
    private String fileUrl;//图片URL
    private boolean showFile;
    private String orderRemark;
    private String customName;
    private String collectionUserName;
    private String sourceName;
    private Date dependTime;
    private String orgName;
    private String billLookUp;
    private Integer payUserType;

    private Long orgId;
    private Long vehicleOrgId;

    private  Long waitBillingAmount;    //未开票金额
    private  Long alreadyBillingAmount;     //已开票金额
}
