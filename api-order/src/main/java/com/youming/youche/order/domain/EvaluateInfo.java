package com.youming.youche.order.domain;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-05-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class EvaluateInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 业务的主键
     */
    private Long busiId;
    /**
     * 评价业务类型
     */
    private Integer evaluateBusiType;
    /**
     * 评价价格
     */
    private Integer evaluatePrice;
    /**
     * 评价质量
     */
    private Integer evaluateQuality;
    /**
     * 评价服务
     */
    private Integer evaluateService;
    /**
     * 操作人
     */
    private Long opId;


}
