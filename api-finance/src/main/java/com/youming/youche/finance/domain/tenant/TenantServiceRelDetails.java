package com.youming.youche.finance.domain.tenant;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 授信额度已用流水表
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantServiceRelDetails extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 对应消费流水 comsume_oil_flow的flow_id
     */
    private Long flowId;

    /**
     * 操作类型:1 已用增加 2已用减少
     */
    private Integer opType;

    /**
     * 业务金额
     */
    private Long amount;

    /**
     * 操作前金额
     */
    private Long beforeAmount;

    /**
     * 操作后金额
     */
    private Long afterAmount;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 渠道
     */
    private String channelType;


}
