package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 订单差异明细
 *
 * @author hzx
 * @date 2022/2/8 9:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderDiffInfo extends BaseDomain {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 差异金额
     */
    private Long diffFee;

    /**
     * 差异说明
     */
    private String diffDesc;

    /**
     * 差异类型（对应静态数据 的 code_type = DIFF_TYPE
     */
    private Integer diffType;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 创建时间
     */
//    private LocalDateTime createDate;

    /**
     * 操作人
     */
    private Long operId;

    /**
     * 操作时间
     */
    private LocalDateTime operDate;

    private Long tenantId;

}
