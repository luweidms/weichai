package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 车队与收款人的关联关系
    * </p>
* @author CaoYaJie
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class TenantReceiverRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 收款人id
            */
    private Long receiverId;

            /**
            * 备注
            */
    private String remark;


}
