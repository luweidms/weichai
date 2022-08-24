package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 应收逾期账单表
 * </p>
 *
 * @author wuhao
 * @since 2022-07-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OverdueReceivable extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 目的地市
     */
    private Integer desRegion;

    /**
     * 起始地市
     */
    private Integer sourceRegion;

    /**
     * 收入（分）
     */
    private Long txnAmt;

    /**
     * 是否确定收款0 否1是 3:司机确认退款
     */
    private Integer payConfirm;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * 付款方名称
     */
    private String name;

    /**
     * 核销金额（分）
     */
    private Long paid;

    /**
     * 业务类型1、订单收入，2、司机借支，3，员工借支
     */
    private Integer type;

    /**
     * 业务编号
     */
    private String businessNumber;

    /**
     * 租户id
     */
    private Long tenantId;

    private Long adminUserId;


}
