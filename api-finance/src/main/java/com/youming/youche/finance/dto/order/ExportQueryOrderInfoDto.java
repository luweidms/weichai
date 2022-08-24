package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 应付管理-账单--导出打印（订单部分）
 *
 * @author hzx
 * @date 2022/4/11 19:23
 */
@Data
public class ExportQueryOrderInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private String dependTime;
    private String billNumber;
    private String costPrice;
    private String incomeExceptionFee;
    private String confirmAmount;
    private String getAmount;
    private String confirmDiffAmount;
    private String orgName;
    private String customName;
    private String customerName;
    private String customerId;
    private String createTime;
    private String sourceRegionName;
    private String desRegionName;

}
