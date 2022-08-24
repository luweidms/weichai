package com.youming.youche.order.domain.oil;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 
    * </p>
* @author WuHao
* @since 2022-04-13
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ClearAccountOilRecord extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账户id
     */
    private Long accId;

    /**
     * 业务类型
     */
    private Integer busiType;

    /**
     * 清除油费金额
     */
    private Long clearAmount;


    /**
     * 订单油来源关系主键id
     */
    private Long fromFlowId;

    private Integer fromTable;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 资金流向批次
     */
    private Long soNbr;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 修改人
     */
    private Long updateOpId;

    /**
     * 用戶id
     */
    private Long userId;


}
