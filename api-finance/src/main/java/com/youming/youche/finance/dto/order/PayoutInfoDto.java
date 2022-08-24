package com.youming.youche.finance.dto.order;

import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.system.domain.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 应收逾期
 *
 * @author hzx
 * @date 2022/4/13 17:15
 */
@Data
public class PayoutInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String createDate; // 创建时间
    private Long orderId; // 订单号
    private Long txnAmt; // 交易金额
    private double paid; // 已打款
    private double notWrittenOff; // 待核销
    private double txnAmtDouble;
    private Long billServiceFee;//票据服务费（路哥）
    private double billServiceFeeDouble;
    private Long appendFreight;//附加运费（56K）
    private double appendFreightDouble;
    private Long sumTxnFee;//（总金额=业务金额+票据服务费+附加运费）
    private double sumTxnFeeDouble;
    private Long linkPhone; // 联系电话
    private Long busiId; // 业务编码
    private String busiIdName; // 业务编码名字
    private String respCode; // 交易编码：0发起成功  1发起失败  2银行打款失败   3提现成功(打款成功) 4网络超时   5(失效)
    private Integer payConfirm; // 是否确认收款 0:否 1:确认中2:已确认
    private String name; // 支付车队名称
    private String stateName; // 状态名称
    private String respCodeName; // 交易编码名称
    private Long flowId; // 流水号
    private Integer verificationState; // 核销状态：1待核销；2已核销；3已撤销'
    private Long sourceRegion; // 始发市
    private Long desRegion; // 到达市
    private String sourceRegionName; // 始发市名称
    private String desRegionName; // 到达市名称
    private Long payTenantId; // 支付车队ID
    private String payTenantName; // 支付车队名称
    private Long userId; // 用户ID
    private Long payObjId;  // 支付方userId
    private Integer userType; // 付款人用户类型
    private Integer isAutoMatic; // 是否线上付款
    private Long objId; // 对象ID
    private Long isNeedBill; // 业务编码
    private String isNeedBillName; // 业务编码名称
    private Date updateDate; // 更新时间
    private Integer bankType; // 银行卡类型：2、对私，1对公 1对公收 11对公付 2对私收 22对私付
    private String accName; // 账号名称
    private String accNo; // 账号
    private Long tenantId; // 车队ID
    private String payConfirmName; // 是否确认收款 0:否 1:确认中2:已确认
    private String bankTypeName; // 银行卡类型名称
    private String isAutomaticName; // 是否为线上打款名称
    private Long noVerificatMoney; // 未核销金额
    private Long verificatMoney; // 已核销金额
    private Integer isDriver; // 是否为司机
    private Integer viewType; // 显示类型
    private Integer accountType; //提现账户类型 1对公收 11对公付 2对私收 22对私付
    private String payBankAccName; // 付款账户名称
    private String remark; // 备注
    private Integer isTurnAutomatic; // 是否为自动打款
    private Date verificationDate; // 核销时间
    private Long subjectsId; // '科目ID '
    private String subjectsIdName; // 科目名称
    private Long oilAffliation; // 资金(油)渠道类型
    private String userName; // 用户名
    private String busiCode; // 业务编码
    private String plateNumber; // 车牌号
    private Integer orderType; // 订单类型
    private String payBankAccNo; // 付款卡号
    private Date payTime; //付款时间
    private String respMsg; // 响应错误消息
    private String receivablesBankAccName; // 收款名称
    private String receivablesBankAccNo; // 收款银行卡号
    private Long vehicleAffiliation; // 资金渠道类型
    private String completeTime; // 完成时间
    private String acctNo; // 卡号
    private String payAccNo; // 付款卡号
    private String accNameAndAccNo; // 卡号和名称
    private String respCodeName100; // 交易编码
    private Date payTime100; // 付款时间
    private Integer virtualState; // 虚拟状态
    private Boolean hasPermission;//是否有审核权限
    private Boolean isNeedPassword;//是否需要密码
    private Boolean isFinallyNode;//是否是最后一个节点
    private String pinganCollectAcctId;//最终收款账户虚拟卡号
    private Long fileId;//图片Id
    private String fileUrl;//图片URL
    private boolean showFile; // 是否显示文件
    private String orderRemark; // 订单备注
    private String customName; // 客户名称
    private String collectionUserName; // 用户名集合
    private String sourceName; // 线路名称
    private String dependTime; // 靠台时间
    private String orgName; // 组织名称
    private String billLookUp; // 收票主体
    private Integer payUserType; // 付款用户类型
    private List<SysOperLog> sysOperLogs; //日志

    private Integer orderState; // 订单状态
    private Integer balanceType; // 结算方式
    private String createTime; // 全款 1 创建时间
    private Integer collectionTime; // 收款期限 预付+尾款账期 2
    private String updateTime; // 审核通过后的时间
    private Integer collectionMonth; // 预付+尾款月结 3 收款月
    private Integer collectionDay; // 预付+尾款月结 3 收款天
    private String carDependDate; // 靠台时间
    /**
     * 应收日期
     * 预付全款——》当天时间
     * 预付加尾款帐期--》回单审核通过后+收款期限 -- 审核通过后的订单修改时间 审核不通过的数据不统计 订单已完成 orderState == 14
     * 预付加尾款月结--》订单靠台月+收款期限 -- 订单靠台后，取靠台时间，靠台状态 orderState == 7（待装货） > 7
     */
    private String receivableDate;

    public double getTxnAmtDouble() {
        if (txnAmt != null) {
            setTxnAmtDouble(CommonUtil.getDoubleFormatLongMoney(txnAmt, 2));
        }
        return txnAmtDouble;
    }

    public double getBillServiceFeeDouble() {
        if (billServiceFee != null) {
            setBillServiceFeeDouble(CommonUtil.getDoubleFormatLongMoney(billServiceFee, 2));
        }
        return billServiceFeeDouble;
    }

    public double getAppendFreightDouble() {
        if (appendFreight != null) {
            setAppendFreightDouble(CommonUtil.getDoubleFormatLongMoney(appendFreight, 2));
        }
        return appendFreightDouble;
    }

    public Long getSumTxnFee() {
        setSumTxnFee((txnAmt == null ? 0L : txnAmt) + (billServiceFee == null ? 0L : billServiceFee) + (appendFreight == null ? 0L : appendFreight));
        return sumTxnFee;
    }

    public double getSumTxnFeeDouble() {
        if (sumTxnFee != null) {
            setSumTxnFeeDouble(CommonUtil.getDoubleFormatLongMoney(sumTxnFee, 2));
        }
        return sumTxnFeeDouble;
    }

    /*
        是否确认收款 0:否 1:确认中2:已确认
            确认收入前 未核销金额为逾期金额 已付款金额为0
            确认收入后 已付款金额为逾期金额 未核销金额为0
     */
    public double getPaid() {
        if (getPayConfirm() == 2) {
            setPaid(getTxnAmtDouble());
        } else {
            setPaid(0.0);
        }
        return paid;
    }

    public double getNotWrittenOff() {
        if (getPayConfirm() == 2) {
            setNotWrittenOff(0.0);
        } else {
            setNotWrittenOff(getTxnAmtDouble());
        }
        return notWrittenOff;
    }

}
