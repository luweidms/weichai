package com.youming.youche.market.domain.youka;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 油卡充值代金券使用记录表
    * </p>
* @author Terry
* @since 2022-03-08
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class VoucherInfoRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 油卡充值记录表id
            */
    private Long rechargeInfoRecordId;

            /**
            * 代金券表id
            */
    private Long voucherInfoId;

            /**
            * 使用金额（单位分）
            */
    private Long amount;

            /**
            * 代金券归属租户id
            */
    private Long tenantId;

            /**
            * 创建时间
            */
    private LocalDateTime createDate;

            /**
            * 修改时间
            */
    private LocalDateTime updateDate;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * null初始化，1成功，2失败
            */
    private Integer isReport;

            /**
            * 处理描述
            */
    private String reportRemark;

    /**
     * 处理时间
     */
    private LocalDateTime reportTime;


}
