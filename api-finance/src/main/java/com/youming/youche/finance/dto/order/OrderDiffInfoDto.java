package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/2/15 14:05
 */
@Data
public class OrderDiffInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;//订单ID
    private Long diffFee;// 金额
    private String diffDesc;//说明
    private Integer diffType;//类型
    private String createDate;//创建时间
    private String creatorName;//创建者名称
    private Double diffFeeDouble;//金额
    private String diffTypeName;//类型名称
    private Long diffId;//id
    private Long creatorId;//创建用户ID
    private Long operId;//更新ID
    private String operDate;//更新时间
    private Long tenantId;//租户ID

}
