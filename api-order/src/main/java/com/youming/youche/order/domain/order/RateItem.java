package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 费率设置项（字表）
    * </p>
* @author CaoYaJie
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class RateItem extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 费率id
     */
    private Long rateId;

            /**
            * 左端点（必须等于前一项的右端点，没有前一项时，为0）
            */
    private Double startValue;

            /**
            * 右端点
            */
    private Double endValue;

            /**
            * 费率
            */
    private Double rateValue;

        @TableField("ITEM_ID")
    private Long itemId;


}
