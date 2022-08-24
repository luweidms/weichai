package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
 * <p>
 * 订单-回单
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderReceipt extends BaseDomain {

    private static final long serialVersionUID = 1L;


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


}
