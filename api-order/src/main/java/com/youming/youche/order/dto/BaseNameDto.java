package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class BaseNameDto implements Serializable {
    /**
     * 账户状态
     */
    private String accState;
    /**
     * 用户状态
     */
    private String userState;
    /**
     * 用户类型：1-主驾驶 2-副驾驶 3- 经停驾驶 4-车队
     */
    private String userType;
    /**
     * 应收逾期
     */
    private Long receivableOverdueBalance;
    /**
     * 应付逾期金额
     */
    private Long payableOverdueBalance;
    /**
     * 加油使用未到期金额(分)
     */
    private String marginBalance;
    /**
     * 油账户明细 返回 值
     */
    private String oilBalance;
    /**
     * ETC金额
     */
    private String etcBalance;
    /**
     * 维修基金
     */
    private String repairFund;
    /**
     * 油卡抵押金额
     */
    private String pledgeOilCardFee;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 类型
     */
    private Long type;
    /**
     * 手机
     */
    private String phone;
    /**
     * 姓名
     */
    private String name;
}
