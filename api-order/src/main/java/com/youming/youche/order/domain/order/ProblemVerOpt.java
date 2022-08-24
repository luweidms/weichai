package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author liangyan
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ProblemVerOpt extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 审核意见
     */
    private String checkRemark;

    /**
     * 审核人
     */
    private Long checkUserId;

    /**
     * 审核人姓名
     */
    private String checkUserName;

    /**
     * 异常处理金额
     */
    private Long problemDealPrice;

    /**
     * 异常ID
     */
    private Long problemId;

    /**
     * 审核前异常状态
     */
    private Integer problemState;

    /**
     * 审核状态 1通过 2驳回
     */
    private Integer state;

    /**
     * 租户ID
     */
    private Long tenantId;


}
