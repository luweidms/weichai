package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.youming.youche.conts.EnumConsts.SysStaticData.BALANCE_TYPE;

/**
* <p>
    * 订单收入账期表
    * </p>
* @author CaoYaJie
* @since 2022-03-17
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderPaymentDaysInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单号
            */
    private Long orderId;

            /**
            * 结算方式 枚举:balance_type
            */
    @SysStaticDataInfoDict(dictDataSource = BALANCE_TYPE)
    private Integer balanceType;

            /**
            * 账期类型:1 成本 2 收入
            */
    private Integer paymentDaysType;

            /**
            * 回单期限
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveTime;

            /**
            * 开票期限
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceTime;

            /**
            * 收款期限
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer collectionTime;

            /**
            * 对账期限
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reconciliationTime;

            /**
            * 回单月份
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveMonth;

            /**
            * 开票月份
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceMonth;

            /**
            * 收款月份
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer collectionMonth;

            /**
            * 对账月份
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reconciliationMonth;

            /**
            * 回单日
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveDay;

            /**
            * 开票日
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceDay;

            /**
            * 收款日
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer collectionDay;

            /**
            * 对账日
            */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reconciliationDay;

            /**
            * 操作时间
            */

    private LocalDateTime opDate;

            /**
            * 操作人
            */
    private Long opId;

            /**
            * 租户id
            */
    private Long tenantId;

    /**
     * 结算方式 枚举:balance_type
     */
    @TableField(exist = false)
    private String balanceTypeName;


}
