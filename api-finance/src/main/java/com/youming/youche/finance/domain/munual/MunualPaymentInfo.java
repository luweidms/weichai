package com.youming.youche.finance.domain.munual;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 现金打款
 *
 * @author zengwen
 * @date 2022/4/7 9:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MunualPaymentInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;//提现时间
    private Long orderId;//订单编号
    private Long txnAmt;//交易金额
    private double txnAmtDouble;//交易金额
    private Long billServiceFee;//票据服务费（路哥）
    private double billServiceFeeDouble;
    private Long appendFreight;//附加运费（56K）
    private double appendFreightDouble;
    private Long sumTxnFee;//（总金额=业务金额+票据服务费+附加运费）
    private double sumTxnFeeDouble;
    private Long linkPhone;//手机号码
    private Long busiId;//业务ID
    private String busiIdName;//业务名称
    private String respCode;//交易状态: 0-发起成功 1-发起失败 2-银行打款失败 3-打款成功 4-网络超时 5-失效
    private Integer payConfirm;//是否确认收款 0:否 1:确认中2:已确认
    private String name;
    private String stateName;//状态名称
    private String respCodeName;//交易状态名称
    private Long flowId;//支出ID
    private Integer verificationState;//打款状态
    private Long sourceRegion;//始发市
    private Long desRegion;//到达市
    private String sourceRegionName;//始发市名称
    private String desRegionName;//到达市名称
    private Long payTenantId;//支付车队
    private String payTenantName;//支付车队名称
    private Long userId;//用户ID
    private Long payObjId;//打款对象ID
    private Integer userType;//收款人用户类型
    private Integer isAutoMatic;//是否系统自动打款,0手动核销，1系统自动打款
    private Long objId;//对象ID
    private Long isNeedBill;//业务编码
    private String isNeedBillName;//业务编码名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;//核销时间
    private Integer bankType;//银行卡类型：2、对私，1对公 1对公收 11对公付 2对私收 22对私付
    private String accName;//收款银行卡账户名
    private String accNo;//收款银行卡账号
    private Long tenantId;//租户ID
    private String payConfirmName;//付款状态
    private String bankTypeName;//银行卡类型名称
    private String isAutomaticName;//是否是系统自动打款说明
    private Long noVerificatMoney;//未核销总金额
    private Long verificatMoney;//已核销总金额
    private Integer isDriver;//收款款对象类型 0司机 1服务商 2租户id 3HA虚拟 4HA实体
    private Integer viewType;
    private Integer accountType;//提现账户类型 1对公收 11对公付 2对私收 22对私付
    private String payBankAccName;//付款银行卡账户名
    private String remark;//备注
    private Integer isTurnAutomatic;//是否为手动转为系统自动打款 0:不是 1:是
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verificationDate;//核销时间
    private Long subjectsId;//科目ID
    private String subjectsIdName;//科目名称
    private Long oilAffliation;//资金(油)渠道类型
    private String userName;//联系人名称
    private String busiCode;//业务编码
    private String plateNumber;//车牌号
    private Integer orderType;
    private String payBankAccNo;//付款卡号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;//支付时间
    private String respMsg;//描述
    private String receivablesBankAccName;//收款名称
    private String receivablesBankAccNo;//收款卡号
    private Long vehicleAffiliation;//资金渠道类型
    private String completeTime;//完成时间
    private String acctNo;//账号
    private String payAccNo;//打款银行账号
    private String accNameAndAccNo;
    private String respCodeName100;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime100;
    private Integer virtualState;//是否为虚拟状态：1-是 0-不是
    private Boolean hasPermission;//是否有审核权限
    private Boolean isNeedPassword;//是否需要密码
    private Boolean isFinallyNode;//是否是最后一个节点
    private String pinganCollectAcctId;//最终收款账户虚拟卡号
    private Long fileId;//图片Id
    private String fileUrl;//图片URL
    private boolean showFile;//是否显示图片
    private String orderRemark;//订单备注
    private String customName;//客户名称
    private String collectionUserName;//代收人名称
    private String sourceName;//线路名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dependTime;//靠台时间
    private String orgName;//组织名称
    private String billLookUp;//收票主体
    private Integer payUserType;

    public double getTxnAmtDouble() {
        if(txnAmt != null){
            setTxnAmtDouble(CommonUtil.getDoubleFormatLongMoney(txnAmt, 2));
        }
        return txnAmtDouble;
    }

    public double getBillServiceFeeDouble() {
        if(billServiceFee != null){
            setBillServiceFeeDouble(CommonUtil.getDoubleFormatLongMoney(billServiceFee, 2));
        }
        return billServiceFeeDouble;
    }

    public double getAppendFreightDouble() {
        if(appendFreight != null){
            setAppendFreightDouble(CommonUtil.getDoubleFormatLongMoney(appendFreight, 2));
        }
        return appendFreightDouble;
    }


    public Long getSumTxnFee() {
        setSumTxnFee((txnAmt == null ? 0L : txnAmt) + (billServiceFee == null ? 0L : billServiceFee) + (appendFreight == null ? 0L : appendFreight));
        return sumTxnFee;
    }

    public double getSumTxnFeeDouble() {
        if(sumTxnFee != null){
            setSumTxnFeeDouble(CommonUtil.getDoubleFormatLongMoney(sumTxnFee, 2));
        }
        return sumTxnFeeDouble;
    }



}
