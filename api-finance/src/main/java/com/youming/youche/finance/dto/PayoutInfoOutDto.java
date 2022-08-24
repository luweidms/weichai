package com.youming.youche.finance.dto;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/4/16
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class PayoutInfoOutDto implements Serializable {
    private LocalDateTime createDate; //创建时间
    private Long orderId; //订单号
    private Long txnAmt; //金额
    private double txnAmtDouble; //金额
    private Long billServiceFee;//票据服务费（路哥）
    private double billServiceFeeDouble;
    private Long appendFreight;//附加运费（56K）
    private double appendFreightDouble;
    private Long sumTxnFee;//（总金额=业务金额+票据服务费+附加运费）
    private double sumTxnFeeDouble; //总基恩
    private Long linkPhone; //联系电话
    private Long busiId; //业务编码
    private String busiIdName; //业务编码名称
    private String respCode; //支付结果
    private Integer payConfirm; //是否确认收款
    private String name; //名字
    private String stateName; //状态名称
    private String respCodeName; //支付结果名称
    private Long flowId;//支付流水号
    private Integer verificationState; //核销状态
    private Long sourceRegion; //始发市
    private Long desRegion; //到达市
    private String sourceRegionName; //始发市名称
    private String desRegionName; //到达市名称
    private Long payTenantId; //付款车队ID
    private String payTenantName; //付款车队名称
    private Long userId; //用户ID
    private Long payObjId; //付款用户
    private Integer userType; //用户类型
    private Integer isAutoMatic; //是否为线上打款
    private Long objId; //对象ID
    private Long isNeedBill; //业务编码
    private String isNeedBillName; //业务编码名称
    private LocalDateTime updateDate; //更新时间
    private Integer bankType; //银行卡类型
    private String accName; //账号名称
    private String accNo; //账号
    private Long tenantId; //车队ID
    private String payConfirmName; //是否确认收款名称
    private String bankTypeName; //银行卡类型名称
    private String isAutomaticName; //是否线上打款名称
    private Long noVerificatMoney; //未核销金额
    private Long verificatMoney; //已核销金额
    private Integer isDriver; //是否为司机
    private Integer viewType; //线上类型
    private Integer accountType; //账号类型
    private String payBankAccName;//支付账户名
    private String remark; //备注
    private Integer isTurnAutomatic; //是否停止自动
    private LocalDateTime verificationDate; //核销时间
    private Long subjectsId; //科目ID
    private String subjectsIdName; //科目名称
    private Long oilAffliation; //资金渠道
    private String userName; //用户名
    private String busiCode; //业务编号
    private String plateNumber; //车牌号
    private Integer orderType; //订单类型
    private String payBankAccNo; //付款卡号
    private LocalDateTime payTime; //付款时间
    private String respMsg; //响应结果描述
    private String receivablesBankAccName; //收款卡名称
    private String receivablesBankAccNo; //收款卡号
    private Long vehicleAffiliation; //资金渠道
    private String completeTime; //完成时间
    private String acctNo; //卡号
    private String payAccNo; //付款卡号
    private String accNameAndAccNo; //卡号和名称
    private String respCodeName100; //支付结果名称
    private LocalDateTime payTime100; //付款时间
    private Integer virtualState; //虚拟类型
    private Boolean hasPermission;//是否有审核权限
    private Boolean isNeedPassword;//是否需要密码
    private Boolean isFinallyNode;//是否是最后一个节点
    private String pinganCollectAcctId;//最终收款账户虚拟卡号
    private Long fileId;//图片Id
    private String fileUrl;//图片URL
    private boolean showFile; //是否线上图片
    private String orderRemark; //订单描述
    private String customName; //客户名称
    private String collectionUserName; //代收人名称
    private String sourceName; //线路名称
    private LocalDateTime dependTime; //靠台时间
    private String orgName; //组织ID
    private String billLookUp; //收票主体
    private Integer payUserType; //付款用户类型

    private Long orgId; //组织ID
    private Long vehicleOrgId; //车组织ID

    public double getSumTxnFeeDouble() {
        if (sumTxnFee != null) {
            setSumTxnFeeDouble(CommonUtil.getDoubleFormatLongMoney(sumTxnFee, 2));
        }
        return sumTxnFeeDouble;
    }

    public Long getSumTxnFee() {
        setSumTxnFee((txnAmt == null ? 0L : txnAmt) + (billServiceFee == null ? 0L : billServiceFee) + (appendFreight == null ? 0L : appendFreight));
        return sumTxnFee;
    }

    public double getAppendFreightDouble() {
        if (appendFreight != null) {
            setAppendFreightDouble(CommonUtil.getDoubleFormatLongMoney(appendFreight, 2));
        }
        return appendFreightDouble;
    }

    public double getBillServiceFeeDouble() {
        if (billServiceFee != null) {
            setBillServiceFeeDouble(CommonUtil.getDoubleFormatLongMoney(billServiceFee, 2));
        }
        return billServiceFeeDouble;
    }

    public double getTxnAmtDouble() {
        if (txnAmt != null) {
            setTxnAmtDouble(CommonUtil.getDoubleFormatLongMoney(txnAmt, 2));
        }
        return txnAmtDouble;
    }

}
