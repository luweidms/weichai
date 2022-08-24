package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

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
    public class OilRechargeAccountDetailsFlowExt extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 匹配流水信息
     */
    private String matchInfo;

    /**
     * oil_recharge_account_details_flow表的主键
     */
    private Long relId;


}
