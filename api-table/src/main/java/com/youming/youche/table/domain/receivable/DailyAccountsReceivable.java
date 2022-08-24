package com.youming.youche.table.domain.receivable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Table;

/**
 * <p>
 * 应收日报
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DailyAccountsReceivable extends BaseDomain {

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 客户全称
     */
    private String customerFullName;

    /**
     * 应收日期
     * <p>
     * 预付全款——》当天时间
     * 预付加尾款帐期--》回单审核通过后+收款期限 -- 审核通过后的订单修改时间 审核不通过的数据不统计 订单已完成 orderState == 14
     * 预付加尾款月结--》订单靠台月+收款期限 -- 订单靠台后，取靠台时间，靠台状态 orderState == 7（待装货） > 7
     */
    private String receivableDate;

    /**
     * 应收金额
     */
    private Long amountReceivable;
    @TableField(exist = false)
    private Double amountReceivableDouble;

    /**
     * 已收（正常）
     */
    private Long receivedNormal;
    @TableField(exist = false)
    private Double receivedNormalDouble;

    /**
     * 已收（逾期）
     */
    private Long receivedOverdue;
    @TableField(exist = false)
    private Double receivedOverdueDouble;

    /**
     * 未收（正常）
     */
    private Long uncollectedNormal;
    @TableField(exist = false)
    private Double uncollectedNormalDouble;

    /**
     * 未收（逾期）
     */
    private Long uncollectedOverdue;
    @TableField(exist = false)
    private Double uncollectedOverdueDouble;

    private Long tenantId;


    /**
     * 所属月份
     */
    @TableField(exist = false)
    private String month;

    public String getMonth() {
        if (receivableDate == null) {
            return "";
        }
        String[] split = receivableDate.split("-");
        return split[0] + "-" + split[1];
    }

    public Double getAmountReceivableDouble() {
        if (getAmountReceivable() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getAmountReceivable(), 2);
    }

    public Double getReceivedNormalDouble() {
        if (getReceivedNormal() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getReceivedNormal(), 2);
    }

    public Double getReceivedOverdueDouble() {
        if (getReceivedOverdue() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getReceivedOverdue(), 2);
    }

    public Double getUncollectedNormalDouble() {
        if (getUncollectedNormal() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getUncollectedNormal(), 2);
    }

    public Double getUncollectedOverdueDouble() {
        if (getUncollectedOverdue() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getUncollectedOverdue(), 2);
    }

}
