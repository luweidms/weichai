package com.youming.youche.table.domain.workbench;

    import java.math.BigDecimal;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 老板工作台
    * </p>
* @author zengwen
* @since 2022-05-09
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class BossWorkbenchInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 租户ID
            */
    private Long tenantId;

            /**
            * 付款审核数量
            */
    private Long paymentReviewAmount;

            /**
            * 车辆费用-ETC
            */
    private BigDecimal carCostEtc;

            /**
            * 车辆费用-油费
            */
    private BigDecimal carCostOil;

            /**
            * 车辆费用-维保费
            */
    private BigDecimal carCostMaintenance;

            /**
            * 车辆费用-杂费
            */
    private BigDecimal carCostOther;


}
