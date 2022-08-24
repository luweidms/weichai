package com.youming.youche.order.domain.order;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支出接口表(提现接口表)
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PayoutIntf extends BaseDomain {

    private static final long serialVersionUID = 1L;


    @TableField("BATCH_SEQ")
    private String batchSeq;

    /**
     * 根据不同支付网关的协议设置: 0(不开票) 1(对方开票) 2(平台开票)
     */
    @TableField("TXN_TYPE")
    private String txnType;

    /**
     * 见sys_static_data.code_type = 'OBJ_TYPE'
     */
    @TableField("OBJ_TYPE")
    private String objType;

    /**
     * 对象ID
     */
    @TableField("OBJ_ID")
    private Long objId;

    private Long userId;

    /**
     * 银行名称
     */
    @TableField("BANK_CODE")
    private String bankCode;

    /**
     * 省份
     */
    @TableField("PROVINCE")
    private String province;

    /**
     * 地市
     */
    @TableField("CITY")
    private String city;

    /**
     * 账号
     */
    @TableField("ACC_NO")
    private String accNo;

    /**
     * 账户名称
     */
    @TableField("ACC_NAME")
    private String accName;

    /**
     * 交易金额，单位分
     */
    @TableField("TXN_AMT")
    private Long txnAmt;

    /**
     * (重要)发起支付时间,调用路歌发起支付记录这个时间，有值则不继续发起支付
     */
    @TableField("PAY_TIME")
    private LocalDateTime payTime;

    /**
     * 0发起成功  1发起失败  2银行打款失败   3提现成功(打款成功) 4网络超时   5(失效)
     */
    @TableField("RESP_CODE")
    private String respCode;

    /**
     * 响应错误消息
     */
    @TableField("RESP_MSG")
    private String respMsg;

    /**
     * 响应时间YYYYMMDDhhmmss
     */
    @TableField("COMPLETE_TIME")
    private String completeTime;

    /**
     * 对账金额
     */
    @TableField("SETTLE_AMT")
    private Long settleAmt;

    /**
     * 对账日期YYYYMMDD
     */
    @TableField("SETTLE_DATE")
    private String settleDate;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 对账结果
     */
    @TableField("BALANCE_RESULT")
    private Boolean balanceResult;

    /**
     * 提现时间
     */
    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    /**
     * 租户ID
     */
    @TableField("TENANT_ID")
    private Long tenantId;

    /**
     * 余额不足，发起打款时间
     */
    @TableField("APP_DATE")
    private LocalDateTime appDate;

    /**
     * 1待核销；2已核销；3已撤销
     */
    @TableField("VERIFICATION_STATE")
    private Integer verificationState;

    @TableField("VERIFICATION_DATE")
    private LocalDateTime verificationDate;

    /**
     * 被托管提现人手机号
     */
    @TableField("OUT_BILL_ID")
    private String outBillId;

    private Long outUserId;

    /**
     * 来源备注(code_type=SOURCE_REMARK)
     */
    @TableField("SOURCE_REMARK")
    private Integer sourceRemark;

    /**
     * 是否对账（1已对账 null or 0 未对账）
     */
    @TableField("IS_CLEAN")
    private Integer isClean;

    /**
     * 资金渠道类型
     */
    @TableField("VEHICLE_AFFILIATION")
    private String vehicleAffiliation;

    /**
     * 提现渠道(1:油老板-微信调用 2:APP调用)
     */
    @TableField("WITHDRAWALS_CHANNEL")
    private String withdrawalsChannel;

    /**
     * 订单编号
     */
    @TableField("ORDER_ID")
    private Long orderId;

    /**
     * 已开票金额
     */
    @TableField("ALREADY_BILLING_AMOUNT")
    private Long alreadyBillingAmount;

    /**
     * 待开票金额
     */
    @TableField("WAIT_BILLING_AMOUNT")
    private Long waitBillingAmount;

    /**
     * 根据支付结果写入订单限制表处理时间
     */
    @TableField("HANDLE_DATE")
    private LocalDateTime handleDate;

    /**
     * 银行支付流水号
     */
    @TableField("SERIAL_NUMBER")
    private String serialNumber;

    /**
     * 路歌运单唯一标识
     */
    @TableField("XID")
    private Long xid;

    /**
     * 修改操作员id
     */
    @TableField("UPDATE_OP_ID")
    private Long updateOpId;

    /**
     * 操作员id
     */
    @TableField("OP_ID")
    private Long opId;

    /**
     * 打款租户id
     */
    @TableField("PAY_TENANT_ID")
    private Long payTenantId;

    /**
     * 是否系统自动打款,0手动核销，1系统自动打款
     */
    @TableField("IS_AUTOMATIC")
    private Integer isAutomatic;

    /**
     * 银行卡类型：2、对私，1对公 1对公收 11对公付 2对私收 22对私付
     */
    @TableField("BANK_TYPE")
    private Integer bankType;

    /**
     * 收款款对象类型 0司机 1服务商 2租户id 3HA虚拟 4HA实体
     */
    @TableField("IS_DRIVER")
    private Integer isDriver;

    /**
     * 是否为手动转为系统自动打款 0:不是 1:是
     */
    @TableField("IS_TURN_AUTOMATIC")
    private Integer isTurnAutomatic;

    /**
     * 费用类型 1现金，2ETC，3实体油，4虚拟油
     */
    @TableField("FEE_TYPE")
    private Integer feeType;

    /**
     * 打款对象ID
     */
    @TableField("PAY_OBJ_ID")
    private Long payObjId;

    /**
     * 打款对象类型 0司机 1服务商 2租户id 3HA虚拟 4HA实体
     */
    @TableField("PAY_TYPE")
    private Integer payType;

    /**
     * 打款银行账号
     */
    @TableField("PAY_ACC_NO")
    private String payAccNo;

    /**
     * 业务ID 对应EnumConsts.PayInter
     */
    @TableField("BUSI_ID")
    private Long busiId;

    /**
     * 科目ID subjects_info表
     */
    @TableField("SUBJECTS_ID")
    private Long subjectsId;

    /**
     * 付款的优先级别
     */
    @TableField("PRIORITY_LEVEL")
    private Integer priorityLevel;

    /**
     * 平台服务费
     */
    @TableField("PLATFORM_SERVICE_FEE")
    private Long platformServiceFee;

    /**
     * 是否确认收款 0:否 1:确认中2:已确认
     */
    @TableField("PAY_CONFIRM")
    private Integer payConfirm;

    /**
     * 提现账户类型 1对公收 11对公付 2对私收 22对私付
     */
    @TableField("ACCOUNT_TYPE")
    private Integer accountType;

    /**
     * 付款账户名
     */
    @TableField("PAY_ACC_NAME")
    private String payAccName;

    /**
     * 付款银行卡账户名
     */
    @TableField("PAY_BANK_ACC_NAME")
    private String payBankAccName;

    /**
     * 付款银行卡账号
     */
    @TableField("PAY_BANK_ACC_NO")
    private String payBankAccNo;

    /**
     * 收款银行卡账户名
     */
    @TableField("RECEIVABLES_BANK_ACC_NAME")
    private String receivablesBankAccName;

    /**
     * 收款银行卡账号
     */
    @TableField("RECEIVABLES_BANK_ACC_NO")
    private String receivablesBankAccNo;

    /**
     * 重新发起的FLOW_ID
     */
    @TableField("RE_FLOW_ID")
    private Long reFlowId;

    /**
     * 二级编码 2.1余额不足(不需要重新生成) 2.2其他错误(需要重新生成记录) 2.3本地业务异常(不需要重新生成记录)
     */
    @TableField("RESP_BANK_CODE")
    private Double respBankCode;

    /**
     * 最终收款人ID
     */
    @TableField("REL_USER_ID")
    private Long relUserId;

    /**
     * 资金(油)渠道类型
     */
    @TableField("OIL_AFFILIATION")
    private String oilAffiliation;

    /**
     * 业务编码
     */
    @TableField("BUSI_CODE")
    private String busiCode;

    /**
     * 调用银行接口的备注
     */
    @TableField("BANK_REMARK")
    private String bankRemark;

    /**
     * 最终收款账户虚拟卡号
     */
    @TableField("PINGAN_COLLECT_ACCT_ID")
    private String pinganCollectAcctId;

    /**
     * 收款人用户类型
     */
    private Integer userType;

    /**
     * 付款人用户类型
     */
    private Integer payUserType;

    /**
     * 开票服务费
     */
    @TableField("BILL_SERVICE_FEE")
    private Long billServiceFee;

    /**
     * 附加运费金额
     */
    @TableField("APPEND_FREIGHT")
    private Long appendFreight;


    @TableField(exist = false)
    private String plateNumber;//车牌号

    @TableField(exist = false)
    private Integer isNeedBill;//票据类型


}
