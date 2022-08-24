package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单同步表
 * </p>
 *
 * @author hzx
 * @since 2022-03-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderSyncTypeInfo extends BaseDomain {

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 同步类型:sync_type
     */
    private Integer syncType;

    /**
     * 票据类型:bill_type
     */
    private Integer billType;

    /**
     * 0:未处理 1:已处理
     */
    private Integer taskDisposeSts;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 进程处理信息
     */
    private String taskDisposeMsg;


}
