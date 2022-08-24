package com.youming.youche.table.domain.receivable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MonthlyReportReceivable extends BaseDomain {

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 客户全称
     */
    private String customerFullName;

    /**
     * 月份("2022-05"),当前统计数据的月份
     */
    private String monthlyReportTime;

    /**
     * 未收(最近1个月)
     */
    private Long uncollected1;

    /**
     * 已收(最近1个月)
     */
    private Long received1;

    /**
     * 未收(最近2个月)
     */
    private Long uncollected2;

    /**
     * 已收(最近2个月)
     */
    private Long received2;

    /**
     * 未收(最近3个月)
     */
    private Long uncollected3;

    /**
     * 已收(最近3个月)
     */
    private Long received3;

    /**
     * 未收(最近4个月)
     */
    private Long uncollected4;

    /**
     * 已收(最近4个月)
     */
    private Long received4;

    /**
     * 未收(最近5个月)
     */
    private Long uncollected5;

    /**
     * 已收(最近5个月)
     */
    private Long received5;

    /**
     * 未收(最近6个月)
     */
    private Long uncollected6;

    /**
     * 已收(最近6个月)
     */
    private Long received6;

    /**
     * 未收（六个月前）
     */
    private Long uncollectedOther;

    /**
     * 已收（六个月前）
     */
    private Long receivedOther;

    private Long tenantId;

    /**
     * 合计
     */
    @TableField(exist = false)
    private Long total;

    public Long getTotal() {
        setTotal(this.uncollected1 + this.received1 +
                this.uncollected2 + this.received2 +
                this.uncollected3 + this.received3 +
                this.uncollected4 + this.received4 +
                this.uncollected5 + this.received5 +
                this.uncollected6 + this.received6 +
                this.uncollectedOther + this.receivedOther);
        return total;
    }

    public Long getCustomerId() {
        if (this.customerId == 0L) {
            setCustomerFullName("三方");
        }
        return customerId;
    }

}
