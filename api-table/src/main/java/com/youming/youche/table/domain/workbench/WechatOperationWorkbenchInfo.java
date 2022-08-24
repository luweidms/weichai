package com.youming.youche.table.domain.workbench;

    import java.math.BigDecimal;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-05-25
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class WechatOperationWorkbenchInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 车队id
     */
    private Long tenantId;

            /**
            * 类型：1-日报  2-月报
            */
    private Integer type;

            /**
            * 日期
            */
    private String time;

            /**
            * 自有收入
            */
    private BigDecimal ownIncome;

            /**
            * 自有成本
            */
    private BigDecimal ownCost;

            /**
            * 自有毛利
            */
    private BigDecimal ownGrossMargin;

            /**
            * 油费
            */
    private BigDecimal oilFee;

            /**
            * 过路费
            */
    private BigDecimal tollFee;

            /**
            * 工资/补贴：type =1 [补贴]  type=2 [工资]
            */
    private BigDecimal wageFee;

            /**
            * 外调收入
            */
    private BigDecimal diversionIncome;

            /**
            * 外调成本
            */
    private BigDecimal diversionCost;

            /**
            * 外调毛利
            */
    private BigDecimal diversionGrossMargin;

            /**
            * 招商收入
            */
    private BigDecimal merchantsIncome;

            /**
            * 招商成本
            */
    private BigDecimal merchantsCost;

            /**
            * 招商毛利
            */
    private BigDecimal merchantsGrossMargin;


}
