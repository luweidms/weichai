package com.youming.youche.finance.domain.ac;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 驾驶员工资发送订单表
    * </p>
* @author zengwen
* @since 2022-06-29
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmSalarySendOrderInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 发送ID
            */
    private Long sendId;

            /**
            * 订单号
            */
    private Long orderId;


}
