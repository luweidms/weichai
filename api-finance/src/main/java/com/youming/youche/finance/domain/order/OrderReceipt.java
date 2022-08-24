package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单-回单
 *
 * @author hzx
 * @date 2022/2/8 9:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderReceipt extends BaseDomain {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 回单编号
     */
    private String reciveNumber;

    /**
     * 图片ID
     */
    private String flowId;

    /**
     * 图片路径
     */
    private String flowUrl;

    /**
     * 操作时间
     */
//    private LocalDateTime createDate;

}
