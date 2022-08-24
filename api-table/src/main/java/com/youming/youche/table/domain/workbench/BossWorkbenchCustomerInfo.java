package com.youming.youche.table.domain.workbench;

    import java.math.BigDecimal;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 老板工作台   客户收入排行表
    * </p>
* @author zengwen
* @since 2022-05-09
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class BossWorkbenchCustomerInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 顾客ID
     */
    private Long customerId;

            /**
            * 客户名称
            */
    private String customerName;

            /**
            * 客户收入
            */
    private BigDecimal businessIncome;


}
