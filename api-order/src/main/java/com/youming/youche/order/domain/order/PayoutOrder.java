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
    public class PayoutOrder extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 匹配金额
     */
    private Long amount;
    /**
     * 费用类型
     */
    private Integer amountType;
    /**
     * 批次号
     */
    private Long batchId;
    /**
     * 是否进入报表,NULl初始化,1成功进入报表,2失败
     */
    private Integer isReport;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 处理描述
     */
    private String reportRemark;
    /**
     * 处理时间
     */
    private LocalDateTime reportTime;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 资金渠道
     */
    private String vehicleAffiliation;

}
