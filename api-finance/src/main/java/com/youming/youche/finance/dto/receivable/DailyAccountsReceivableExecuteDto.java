package com.youming.youche.finance.dto.receivable;

import lombok.Data;

import java.io.Serializable;

@Data
public class DailyAccountsReceivableExecuteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId; // 订单id

    private Integer orderState; //订单状态

    private String billNumber; // 账单id

    private Long customUserId; // 客户id（最后获取全称）

    /**
     * 第三方获取收入信息：预付现金、到付、后付现金
     * 非第三方获取：收入金额
     */
    private Long costPrice; // 订单收入

    private Long getAmount; // 核销金额

    private Integer balanceType; // 结算方式

    private String createTime; // 全款 1 创建时间

    private Integer collectionTime; // 收款期限 预付+尾款账期 2

    private String updateTime; // 审核通过后的时间

    private Integer collectionMonth; // 预付+尾款月结 3 收款月

    private Integer collectionDay; // 预付+尾款月结 3 收款天

    private String carDependDate; // 靠台时间

    private Long tenantId; // 车队id

    // ------------------------- 以上是查询结果字段 -------------------------
    /**
     * 应收日期
     * <p>
     * 预付全款——》当天时间
     * 预付加尾款帐期--》回单审核通过后+收款期限 -- 审核通过后的订单修改时间 审核不通过的数据不统计 订单已完成 orderState == 14
     * 预付加尾款月结--》订单靠台月+收款期限 -- 订单靠台后，取靠台时间，靠台状态 orderState == 7（待装货） > 7
     */
    private String receivableDate;

}
