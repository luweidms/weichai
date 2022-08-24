package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderAccountOilSource extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账户表(order_account)ID
     */
    private Long accId;
    /**
     * 油卡金额
     */
    private Long oilBalance;
    /**
     * 操作员id
     */
    private Long opId;
    /**
     * 上级ID
     */
    private Long parentId;
    /**
     * 充值油
     */
    private Long rechargeOilBalance;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 修改操作员id
     */
    private Long updateOpId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户类型
     */
    private Integer userType;


}
