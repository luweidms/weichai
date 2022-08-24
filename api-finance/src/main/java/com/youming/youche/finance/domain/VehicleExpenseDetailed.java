package com.youming.youche.finance.domain;

    import java.io.Serializable;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 车辆费用明细表
    * </p>
* @author liangyan
* @since 2022-04-19
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class VehicleExpenseDetailed extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 申请单号
            */
    private String applyNo;


            /**
            * 费用类型：1维修，2保养，3加油，4杂费
            */
    private Long type;

    /**
     * 费用类型：1维修，2保养，3加油，4杂费
     */
    private String typeName;

            /**
            * 申请人id
            */
    private Long applyId;

            /**
            * 申请人姓名
            */
    private String applyName;

            /**
            * 申请时间
            */
    private LocalDateTime applyTime;

            /**
            * 申请金额(分)
            */
    private Long applyAmount;

            /**
            * 描述
            */
    private String introduce;

            /**
            * 费用使用归属日期
            */
    private LocalDateTime expenseTime;

            /**
            * 附件
            */
    private String file;

    /**
     * 支付方式：1:油卡，2：现金
     */
    private Integer paymentType;
    /**
     * 油卡卡号
     */
    private String oilCardNumber;
    /**
     * 加油里程
     */
    private Long refuelingMileage;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 加油升数
     */
    private Long refuelingLiters;


}
