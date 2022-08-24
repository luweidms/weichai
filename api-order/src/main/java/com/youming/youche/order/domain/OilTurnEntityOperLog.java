package com.youming.youche.order.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
* <p>
    * 
    * </p>
* @author wuhao
* @since 2022-04-25
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilTurnEntityOperLog extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流水金额
     */
    private Long amount;
    /**
     * 批次号
     */
    private Long batchId;
    /**
     * 創建时间
     */
    private Date createDate;
    /**
     * 操作人id
     */
    private Long opId;
    /**
     * 操作人
     */
    private String opName;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 车队id
     */
    private Long tenantId;


}
