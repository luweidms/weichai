package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

    import java.util.List;

/**
* <p>
    * 费率设置
    * </p>
* @author CaoYaJie
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class Rate extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 费率名称
            */
    private String rateName;
    /**
     * 费率id
     */
    private Long rateId;


    @TableField(exist = false)
    private List<RateItem> rateItemList;


}
