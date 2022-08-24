package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderFeeExtH extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 附加运费
     */
    private Long appendFreight;
    /**
     * 默认收票账户
     */
    private String billAcctNo;
    /**
     * 开票抬头
     */
    private String billLookUp;
    /**
     * 副驾驶每天补贴金额
     */
    private Long copilotDaySalary;
    /**
     * 副驾驶工资模式：1普通，2里程，3按趟
     */
    private Long copilotSalary;
    /**
     * 副驾驶工资模式：1普通，2里程，3按趟
     */
    private Integer copilotSalaryPattern;
    /**
     * 副驾补贴具体时间
     */
    private String copilotSubsidyTime;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 司机每天补贴金额
     */
    private Long driverDaySalary;
    /**
     * 切换司机补贴
     */
    private Long driverSwitchSubsidy;
    /**
     * 结束时间
     */
    private LocalDateTime endDate;
    /**
     * 预估成本
     */
    private Long estFee;
    /**
     * 调用路歌同步时间
     */
    private LocalDateTime execDate;
    /**
     * 固定成本
     */
    private Long fixedFee;
    /**
     * 收入到付款
     */
    private Long incomeArrivePaymentFee;
    /**
     * 收入现金费用
     */
    private Long incomeCashFee;
    /**
     * 收入etc费用
     */
    private Long incomeEtcFee;
    /**
     * 收入尾款费用
     */
    private Long incomeFinalFee;
    /**
     * 收入保费
     */
    private Long incomeInsuranceFee;
    /**
     * 收入实体油金额
     */
    private Long incomeOilFee;
    /**
     * 收入虚拟油金额
     */
    private Long incomeOilVirtualFee;
    /**
     * is_need_track_sync
     */
    private Integer isNeedTrackSync;
    /**
     * 主驾驶月工资
     */
    private Long monthSalary;
    /**
     * 副驾驶月工资
     */
    private Long monthSubSalary;
    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private Integer oilAccountType;
    /**
     * 油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
     */
    private Integer oilBillType;
    /**
     * 虚拟油消费对象，1自有油站；2共享油站;
     */
    private Integer oilConsumer;
    /**
     * 实际油卡押金
     */
    private Integer oilDepositActual;
    /**
     * 标准油卡押金
     */
    private Integer oilDepositStandard;
    /**
     * 油费预存资金:0否，1是
     */
    private Integer oilFeePrestore;
    /**
     * 实体油量
     */
    private Long oilLitreEntity;
    /**
     * 总耗油量
     */
    private Long oilLitreTotal;
    /**
     * 虚拟油量
     */
    private Long oilLitreVirtual;
    /**
     * 油单价
     */
    private Long oilPrice;
    /**
     * 油返利
     */
    private Long oilRebate;
    /**
     * 虚拟油返利
     */
    private Long oilRebateVirtual;
    /**
     * 路歌油同步状态
     */
    private String oilSynFlag;
    /**
     * 路歌油同步返回信息
     */
    private String oilSynMsg;
    /**
     * 操作员id
     */
    private Long opId;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 平安付款账户编号
     */
    private String pinganPayAcctId;
    /**
     * 平安付款账户编号
     */
    private String pinganPayAcctName;
    /**
     * 平安付款账户编号
     */
    private String pinganPayAcctNo;
    /**
     * 路桥费
     */
    private Long pontage;
    /**
     * 实际路桥费
     */
    private Long pontageActual;
    /**
     * 实际etc利润
     */
    private Long pontageIncome;
    /**
     * 路桥费的单价
     */
    private Long pontagePer;
    /**
     * 预付etc审核状态 0 为不需审核 1 需要审核
     */
    private Integer preEtcAuditSts;
    /**
     * 预付油审核状态 0 为不需审核 1 需要审核
     */
    private Integer preOilAuditSts;
    /**
     * 预付款审核状态 0 为不需审核 1 需要审核
     */
    private Integer preTotalAuditSts;
    /**
     * 实算副驾驶补贴
     */
    private Long relCopilotSalary;
    /**
     * 实际固定成本
     */
    private Long relFixedFee;
    /**
     * 实算主驾驶补贴
     */
    private Long relSalary;
    /**
     * 实际副驾驶补贴天数
     */
    private Long relSubsidyCopilotDay;
    /**
     * 实际补贴天数
     */
    private Integer relSubsidyDay;
    /**
     * 路歌已经销帐金额
     */
    private Long remainAmount;
    /**
     * 路歌返回码
     */
    private String resultCode;
    /**
     * 路歌返回中文说明
     */
    private String resultInfo;
    /**
     * 主驾驶补贴
     */
    private Long salary;
    /**
     * 主驾驶工资模式：1普通，2里程，3按趟
     */
    private Integer salaryPattern;
    /**
     * 预估副驾驶补贴天数
     */
    private Long subsidyCopilotDay;
    /**
     * 预估补贴天数
     */
    private Integer subsidyDay;
    /**
     * 补贴具体时间
     */
    private String subsidyTime;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 中标价审核状态 0 为不需审核 1 需要审核
     */
    private Integer totalAuditSts;
    /**
     * 更新时间
     */
    private LocalDateTime updateDate;
    /**
     *
     */
    private Long updateOpId;
    /**
     * 第三方唯一标识
     */
    private String xid;


}
