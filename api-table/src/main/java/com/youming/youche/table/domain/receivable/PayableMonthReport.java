package com.youming.youche.table.domain.receivable;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 应付月报表
    * </p>
* @author zengwen
* @since 2022-05-11
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PayableMonthReport extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 车队id
     */
    private Long tenantId;

            /**
            * 业务方
            */
    private Long userId;

            /**
            * 应付时间(月份)
            */
    private String createDate;

            /**
            * 已付
            */
    private Long paidAmount;

            /**
            * 未付
            */
    private Long nopaidAmount;


}
