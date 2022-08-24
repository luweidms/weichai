package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单导入数据容器
 *
 * @author hzx
 * @date 2022/4/9 14:38
 */
@Data
public class OrederExportBillVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String billNumber; //帐单号
    private String orderId; //订单号
    private String billDiff; //对账差异
    private String billDiffRemark; //说明
    private String kpiDiff; //KPI差异
    private String kpiDiffRemark; //说明
    private String oilFeeDiff; //油价差异
    private String oilFeeDiffRemark; //说明
    private String billIngDiff; //开单差异
    private String billIngDiffRemark; //说明
    private String otherDiff; //其它差异
    private String otherDiffRemark; //说明

    private String reasonFailure;// 失败原因

}
