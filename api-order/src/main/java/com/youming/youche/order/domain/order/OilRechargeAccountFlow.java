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
* @author liangyan
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilRechargeAccountFlow extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 母卡编号
     */
    private Long accountId;
    /**
     * 账户类型 1母卡 2子卡
     */
    private Integer accountType;
    /**
     * 交易金额
     */
    private Long amount;
    /**
     * 批次号
     */
    private Long batchId;
    /**
     * 手机号码
     */
    private String billId;
    /**
     * 业务单号
     */
    private String busiCode;
    /**
     * 业务类型名称
     */
    private String busiName;
    /**
     * 业务类型 1充值 2提现 3分配 4撤回 5加油 6返利 7还款 8移除-专票账户
     */
    private Integer busiType;
    /**
     * 渠道标识
     */
    private String channelType;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 当前金额（分）
     */
    private Long currentAmount;
    /**
     * 操作员Id
     */
    private Long opId;
    /**
     * 虚拟账户（如果有多笔，随机取一个虚拟账户）
     */
    private String pinganAccId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 科目
     */
    private Long subjectsId;
    /**
     * 租户Id
     */
    private Long tenantId;
    /**
     * 修改操作员Id
     */
    private Long updateOpId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户名称（母卡显示车队名称）
     */
    private String userName;
    /**
     * 核销状态 0待确认 1已完成 2已核销
     */
    private Integer verifyState;


}
