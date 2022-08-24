package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OilRechargeAccountDto implements Serializable {
    /**
     * 非抵扣油余额
     */
    private Long nonDeductOilBalance;
    /**
     * 抵扣油余额
     */
    private Long deductOilBalance;
    /**
     * 已开票油余额=客户油+转移油+返利油
     */
    private Long custOilBalance;

    private Long rebateOilBalance;

    private Long transferOilBalance;

    private Long invoiceOilBalance;

    private Long billLookUpBalance;

    private Long unBillLookUpBalance;
    /**
     * 油账户明细 返回 值
     */
    private String oilBalance;
    private String tenantTypeName;
    private Long tenantId;
    private String billId;
    private String tenantName;
    private Long userId;//用户编号
    /**
     * 账户明细列表
     */
    private  Long id;//
    private Integer accountType;//账户类型 1母卡 2子卡',
    private String pinganAccId;// 平安虚拟账户ID
    private Integer sourceType;// 充值来源  1返利充值 2现金充值 3授信充值 4继承 5抵扣票现金充值  6转移账户充值
    private Long accountBalance;//账户余额
    private Long distributedAmount;//已分配金额
    private Long unUseredBalance;// 已分配(未消费)余额
    private String opName;// 操作员
    private Long sourceUserId;// 来源用户
    private String sourcePinganAccId;// 来源平安虚拟账户ID
    private Integer state;// '1有效 0无效
    private String vehicleAffiliation;// 资金渠道（只有运输专票才记录，其他的不记录）
    private  Long opId;//操作员ID
    private LocalDateTime creaTeime ;// 创建时间
    private  LocalDateTime updateTime;
    private  Long updateOpId;//修改操作员IDT
    private  Integer channelType;
    private List<BankBalanceInfo> bankBalanceInfos;// 银行列表及每个账户对应余额
    private Long lowestAmount;// 油卡充值最低限制
    private  Long creditBalance;//可用授信额度
    private  Long serviceId;// 服务商Id
    private  String serviceName;//服务商名称
    private AccountBankRel  receivedBankInfo;// 充值收款账户平安信息
}
