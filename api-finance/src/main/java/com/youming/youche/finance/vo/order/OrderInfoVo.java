package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 应收订单
 *
 * @author hzx
 * @date 2022/2/8 9:22
 */
@Data
public class OrderInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId; // 订单号 --
    private String beginDependTime; // 靠台时间 --
    private String endDependTime; // 靠台时间 --
    private String customName; // 客户名称
    private String companyName; // 客户全称 --
    private String checkName; // 对账名称 --
    private String customerIds; // 客户回单号 --
    private Integer orderType; // 订单类型（线路属性） --
    private String billNumber; // 账单号 --
    private String receiptNumber; // 发票号 --
    private Integer isCreateBill; // 是否创建账单 --
    private Integer financeSts; // 核销状态 --
    private Integer isHis; // 是否历史单 0不是，1是
    private Integer orderState; // 订单状态 --
    private Long orgId; // 订单归属部门 --
    private String plateNumber; // 车牌号码 --
    private Long tenantId;
    private String sourceName; // 线路名称 --
    private String customNumber; // 客户单号 --
    private Integer stateType; // 订单类型  --
    private String beginTime; // 应收开始
    private String endTime; // 应收结束

}
