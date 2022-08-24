package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class TenantAgentServiceRel extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 代收服务商id
     */
    private Long agentId;
    /**
     * 結束时间
     */
    private LocalDateTime endTime;
    /**
     * 授信金额
     */
    private Long quotaAmt;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 已使用授信金额
     */
    private Long useQuotaAmt;


}
