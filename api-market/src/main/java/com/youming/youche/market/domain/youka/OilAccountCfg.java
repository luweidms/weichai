package com.youming.youche.market.domain.youka;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author XXX
* @since 2022-03-24
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilAccountCfg extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 代收服务商id
     */
    private Long agentId;
    /**
     * 代收服务商在服务商的账号
     */
    private String agentServiceAcct;
    /**
     * 编码
     */
    private String agentServiceCode;
    /**
     * 代收服务商在服务商的公司名称
     */
    private String agentServiceName;
    /**
     * 服务商类型  1找油 2G7
     */
    private Integer serviceType;


}
