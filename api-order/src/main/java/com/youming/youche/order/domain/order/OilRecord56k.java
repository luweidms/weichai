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
* @author liangyan
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilRecord56k extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 金额
     */
    private Long amount;
    /**
     * 业务编码
     */
    private String busiCode;
    /**
     * 业务类型 1分配 2消费
     */
    private Integer busiType;
    /**
     * 渠道标识
     */
    private String channelType;
    /**
     * 操作时间
     */
    private LocalDateTime dealDate;
    /**
     * 支付标识
     */
    private Integer identifier;
    /**
     * 操作员Id
     */
    private Long opId;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 同步信息
     */
    private String syncMsg;
    /**
     * 同步状态：0初始化 1失败 2成功
     */
    private Integer syncState;
    /**
     * 租户
     */
    private Long tenantId;
    /**
     * 修改操作员Id
     */
    private Long updateOpId;


}
