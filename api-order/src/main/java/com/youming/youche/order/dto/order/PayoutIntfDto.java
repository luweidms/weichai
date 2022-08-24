package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author hzx
 * @date 2022/4/16 11:06
 */
@Data
public class PayoutIntfDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String batchSeq;
    private String txnType; // 0:不开票  1:对方开票  2:平台开票
    private String objType;
    private Long objId;
    private Long userId;//用戶id
    private String bankCode;// 银行名称
    private String province;// 省份
    private String city;// 地市
    private String accNo;// 账号
    private String accName;// 账号名称
    private Long txnAmt;// 交易金额(分)
    private Date payTime;// 发起支付时间
    private String respCode;// 交易状态: 0-发起成功 1-发起失败  2-银行打款失败  3-打款成功 4-网络超时  5-失效
    private String respMsg;// 相应错误信息
    private String completeTime;//响应时间
    private Long settleAmt;// 对账金额
    private String settleDate; // 对账日期
    private String remark;// 备注
    private Integer balanceResult;// 对账结果
    private Date createDate;// 提现时间
    private Long tenantId;// 租户ID
    private Date appDate;// 余额不足 发起打款时间
    private Integer verificationState;// 核销状态：1-待核销  2-已核销  3-已撤销
    private Date verificationDate;// 核销时间
    private String outBillId;// 被托管提现人手机号
    private Long outUserId;//
    private Integer sourceRemark;// 来源备注
    private Integer isClean;// 是否对账：1-已对账 其他：未对账
    private String vehicleAffiliation;//资金渠道类型
    private String withdrawalsChannel;// 提现渠道：1-微信调用 2-APP调用
    private Long orderId;// 订单编号
    private Long alreadyBillingAmount;// 已开票金额
    private Long waitBillingAmount;// 待开票金额
    private Date handleDate;// 根据支付结果写入订单限制表处理时间
    private String serialNumber;// 银行支付流水号
    private Long xid;// 路歌运单唯一表示
    private Long updateOpId;//修改操作员ID
    private Long opId;// 操作员ID
    private Long payTenantId;// 打款租户ID
    private Integer isAutomatic;//是否系统自动打款：0-手动核销 1-系统自动打款
    private Integer bankType;// 银行卡类型：
    private Integer isDriver;// 收款对象类型：0-司机 1-服务商 2-租户ID 3-HA虚拟 4-HA实体
    private Integer isTurnAutomatic;// 是否为手动转为系统自动打款：0-不是  1-是
    private Integer feeType;// 费用类型：1-现金 2-ETC 3-实体油  4-虚拟油
    private Long payObjId;// 打款对象ID
    private String payAccNo;// 打款银行卡号
    private Long busiId;// 业务ID
    private Long subjectsId;// 科目ID
    private Integer priorityLevel;// 付款的优先级别
    private Long platformServiceFee;// 平台服务费
    private Integer payConfirm;// 是否确认收款：0-否 1-确认中  2-已确认
    private Integer accountType;// 提现账户类型：1-对公收 11-对公付 2-对私收 22-对私付
    private String payAccName;// 付款账户名称
    private String payBankAccName;// 付款银行卡账号名
    private String payBankAccNo;// 付款银行卡账号
    private String receivablesBankAccName;// 收款银行卡账号名
    private String receivablesBankAccNo;// 收款银行卡账号
    private Long reFlowId;// 重新发起的Flow Id
    private Double respBankCode;// 二级编码：2.1-余额不足(不需要重新生成)  2.2其他错误(需要重新生成)  2.3-本地业务异常(不需要重新生成记录)
    private Long relUserId;// 最终收款人ID
    private String busiCode;// 业务编码
    private String bankRemark;// 调用银行接口的备注
    private String pinganCollectAcctId;//最终收款账户虚拟卡号
    private Integer userType;// 收款人用户类型
    private Integer payUserType;// 付款人用户类型
    private Long billServiceFee;//票据服务费（路哥）
    private Long appendFreight;//附加运费（56K）
    private Integer payType; // '打款对象类型 0司机 1服务商 2租户id 3HA虚拟 4HA实体',
    private String oilAffiliation; // '资金(油)渠道类型'

    private String plateNumber;//车牌号

    private Integer isNeedBill;//票据类型

    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime UpdateTime;
    private String dependDate;
    private String sourceName;
    /**
     * 公司名称
     */
    private String companyName;

}
