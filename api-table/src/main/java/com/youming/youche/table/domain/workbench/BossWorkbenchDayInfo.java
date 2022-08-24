package com.youming.youche.table.domain.workbench;

    import java.math.BigDecimal;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 老板工作台-每日运用报表数据
    * </p>
* @author zengwen
* @since 2022-05-09
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class BossWorkbenchDayInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 车队id
     */
    private Long tenantId;

            /**
            * 日期
            */
    private String time;

            /**
            * 营业收入
            */
    private BigDecimal businessIncome;

            /**
            * 营业成本
            */
    private BigDecimal businessCost;

            /**
            * 营业毛利
            */
    private BigDecimal businessProfit;


}
