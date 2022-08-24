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
* @since 2022-03-29
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PayCenterAccountInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 开票方式
            */
    private Long billMethodId;

            /**
            * 账户
            */
    private String loginAcct;

            /**
            * 密码
            */
    private String password;

            /**
            * 租户ID
            */
    private Long tenantId;


}
