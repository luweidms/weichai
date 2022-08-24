package com.youming.youche.order.domain.order;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 借支信息表
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OaLoan extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 用户ID
     */
    private Long userInfoId;

    /**
     * 用户名
     */
    private String userInfoName;

    /**
     * 上级部门
     */
    private Long rootOrgId;

    /**
     * 部门
     */
    private Long orgId;

    /**
     * 类别
     */
    private Integer loanType;

    /**
     * 申请时间
     */
    private LocalDateTime appDate;

    /**
     * 借款原因
     */
    private String appReason;

    /**
     * 金额(单位分)
     */
    private Long amount;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 省份
     */
    private String province;

    /**
     * 地市
     */
    private String city;

    /**
     * 账号
     */
    private String accNo;

    /**
     * 帐户名
     */
    private String accName;

    /**
     * 是否分期
     */
    private Integer isAmortize;

    /**
     * 期数
     */
    private Integer repayDays;

    /**
     * 分期开始日期
     */
    private LocalDateTime amortizeDate;

    /**
     * 还款截至日期
     */
    private LocalDateTime repayDate;

    /**
     * 核销金额（分）
     */
    private Long payedAmount;

    /**
     * 状态（待处理，部分核销，已核销）
     */
    private Integer state;

    /**
     * 审批部门
     */
    private Long verifyOrgId;

    /**
     * 审批人（审批人为空则为部门审批）
     */
    private Long verifyOpId;

    /**
     * 流程ＩＤ
     */
    private Long nodeId;

    /**
     * 审批时间
     */
    private LocalDateTime verifyDate;

    /**
     * 审批说明
     */
    private String verifyNotes;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 科目
     */
    private Integer loanSubject;

    /**
     * 资金池余额(分)
     */
    private Long poolBalance;

    /**
     * 资金池金额中已借支的金额
     */
    private Long borrowAmount;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 银行支行
     */
    private String bankBranch;

    /**
     * 司机姓名
     */
    private String carOwnerName;

    /**
     * 车主手机
     */
    private String carPhone;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 借支分类：1原有的借支申请(内部员工),2车管中心借支
     */
    private Integer classify;

    /**
     * 大区ID
     */
    private Long bigRootId;

    /**
     * 申请发起:1车管中心借支，2司机(APP)
     */
    private Integer launch;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 违章罚款原因1、因公 2、因私
     */
    private Integer loanTransReason;

    /**
     * 借支序列id
     */
    private String oaLoanId;

    /**
     * 过磅费重量（千克）
     */
    private String weight;

    /**
     * 资金归属
     */
    private Long fundAllocationOrgId;

    /**
     * 工资抵扣主驾驶借支
     */
    private Long salaryDeduction;

    /**
     * 工资抵扣副驾驶借支
     */
    private Long salaryDeductionSlave;

    /**
     * 核销备注
     */
    private String verifyRemark;

    /**
     * 支付id
     */
    private Long flowId;

    /**
     * 油转现抵扣借支油状态：NULL未被抵扣，0部分被抵扣，1已全部抵扣
     */
    private Integer deductibleState;

    /**
     * 油转现已抵扣借支油金额
     */
    private Long deductibleAmount;

    /**
     * 抵扣时间
     */
    private LocalDateTime deductibleTime;

    /**
     * 借支人姓名
     */
    private String borrowName;

    /**
     * 借支人手机
     */
    private String borrowPhone;

    /**
     * 借支人ID
     */
    private Long borrowUserInfoId;

    /**
     * 借支人属性
     */
    private Integer borrowType;

    /**
     * 事故时间
     */
    private LocalDateTime accidentDate;

    /**
     * 出险时间
     */
    private LocalDateTime insuranceDate;

    /**
     * 事故类型
     */
    private Integer accidentType;

    /**
     * 事故原因
     */
    private Integer accidentReason;

    /**
     * 责任划分
     */
    private Integer dutyDivide;

    /**
     * 事故司机
     */
    private Integer accidentDivide;

    /**
     * 保险公司
     */
    private String insuranceFirm;

    /**
     * 理赔金额
     */
    private Long insuranceMoney;

    /**
     * 事故说明
     */
    private String accidentExplain;

    /**
     * 报案号
     */
    private String reportNumber;

    /**
     * 出险说明
     */
    private String insuranceExplain;

    /**
     * 司机应付逾期支付ID
     */
    private Long payFlowId;

    /**
     * 收款账户类型：0私人账户；1对公账户
     */
    private Integer bankType;

    /**
     * 是否需要开票 0无需 1需要
     */
    private Integer isNeedBill;

    /**
     * 对私收款虚拟账户
     */
    private String collectAcctId;

    /**
     * 付款方类型
     */
    private Integer payUserType;

    /**
     * 收款方类型
     */
    private Integer receUserType;

    /**
     * 借支记录最后核销人id
     */
    private Long verifyUserId;

    /**
     * 借支记录最后核销人姓名
     */
    private String verifyUserName;

    private Long borrowUserId;

    private Integer sts;

    private Long userId;

    private String userName;

    private  Integer identification;
}
