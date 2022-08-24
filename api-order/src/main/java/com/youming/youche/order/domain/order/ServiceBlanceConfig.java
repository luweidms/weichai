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
    public class ServiceBlanceConfig extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 代收服务商id
     */
    private Long agentId;
    /**
     * 用户id
     */
    private Long agentServiceUserId;
    /**
     * 油站id
     */
    private Long productId;
    /**
     * 预存余额
     */
    private Long reserveBalance;
    /**
     * 类型(1服务商  2油站)
     */
    private Integer serviceBlanceType;
    /**
     * 维修商id
     */
    private Long serviceUserId;
    /**
     * 已用余额
     */
    private Long usedBalance;
    /**
     * 预警阀值
     */
    private Long warnBalance;
    /**
     * 接收手机号
     */
    private String warnTels;


}
