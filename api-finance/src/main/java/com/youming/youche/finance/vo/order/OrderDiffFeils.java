package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 对账差异
 *
 * @author hzx
 * @date 2022/2/16 14:48
 */
@Data
public class OrderDiffFeils implements Serializable {

    private static final long serialVersionUID = 1L;

    private String createDate;//创建时间
    private String creatorId;//创建者ID
    private String creatorName;//创建者名称
    private String diffDesc;//说明
    private String diffFee;//金额
    private String diffFeeDouble;//金额
    private Long diffId;//
    private Integer diffType;//类型
    private String diffTypeName;//类型名称
    private String operDate;//修改时间
    private Long operId;//修改ID
    private Long orderId;// 订单ID
    private String tenantId;// 租户ID

}
