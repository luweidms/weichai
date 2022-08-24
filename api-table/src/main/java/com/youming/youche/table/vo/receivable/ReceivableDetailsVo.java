package com.youming.youche.table.vo.receivable;

import lombok.Data;

import java.io.Serializable;

/**
 * 应收详情
 */
@Data
public class ReceivableDetailsVo implements Serializable {

    private static final long serialVersionUID = 5084392888764917017L;

    private String orderId; // 订单号码
    private Integer stateType; // 订单类型
    private Integer orderType; // 线路属性
    private String beginDependTime; // 靠台开始
    private String endDependTime; // 靠台结束
    private Integer orderState; // 订单状态
    private String receiptNumber; // 发票号码
    private String orgId;
    private String orgName; // 归属部门
    private String sourceName; // 线路名称
    private Integer financeSts; // 核销状态
    private String customerIds; // 客户回单号
    private Integer isCreateBill; // 账单生成
    private String billNumber; // 账单号码
    private String customNumber; // 客户单号
    private String plateNumber; // 车牌号码

    private Long tenantId; // 车队id

    private String name; // 客户名称
    private String month; // 应收日期  -> 月份(yyyy-MM)
    private String receivableDate; // 应收日期(yyyy-MM-dd)

}
