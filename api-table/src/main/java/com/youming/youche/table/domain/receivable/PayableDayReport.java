package com.youming.youche.table.domain.receivable;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-05-10
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PayableDayReport extends BaseDomain {

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
            * 付款类型
            */
    private Long busiId;

            /**
            * 应付时间
            */
    private String createDate;

            /**
            * 业务金额
            */
    private Long txnAmount;

            /**
            * 已付正常
            */
    private Long paidNormalAmount;

            /**
            * 已付逾期
            */
    private Long paidOverdueAmount;

            /**
            * 未付正常
            */
    private Long nopaidNormalAmount;

            /**
            * 未付逾期
            */
    private Long nopaidOverdueAmount;


}
