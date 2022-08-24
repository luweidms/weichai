package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 应收账单
 *
 * @author hzx
 * @date 2022/2/8 16:22
 */
@Data
public class OrderBillInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    String beginTime; // 创建开始时间
    String endTime; // 创建结束时间
    String customName; // 对账名称
    String billNumber; // 账单号
    String creatorName; // 创建人
    boolean isAllBill;
    String billSts; // 订单状态

}
