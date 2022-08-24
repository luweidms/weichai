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
* @since 2022-04-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CreditRatingRuleDef extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 等级值，1铜、2银、3金、4钻
     */
    private Integer levelNumber;

    /**
     * 等级范围结束分,-1表示忽略.\n如果min_score和max_score都为-1，表示此等级规则无效
     */
    private Integer maxScore;

    /**
     * 等级范围开始分,-1表示忽略
     */
    private Integer minScore;

    /**
     * 等级名称
     */
    private String ratingName;

    /**
     * 状态1 : 可用  0 : 不可用
     */
    private Integer state;


}
