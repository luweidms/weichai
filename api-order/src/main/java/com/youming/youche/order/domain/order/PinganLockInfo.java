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
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PinganLockInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 业务类型 1电子油 2ETC  -1查询全部
     */
    private Integer businessType;
    /**
     *渠道标识
     */
    private String channelType;
    /**
     * 预存资金
     */
    private Long lockBalance;
    /**
     * 操作人id
     */
    private Long opId;
    /**
     * 操作人
     */
    private String opName;
    /**
     *平安虚拟账户ID
     */
    private String pinganAccId;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 修改人id
     */
    private Long updateOpId;


}
