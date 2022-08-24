package com.youming.youche.order.vo;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderOilCardInfoVo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 油卡号
     */
    private String oilCardNum;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 油费
     */
    private String oilFee;

    /**
     * 油卡类型
     */
    private Integer cardType;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 油卡渠道 0 添加 1 车辆绑定
     */
    private Integer cardChannel;

    /**
     * 理论余额
     */

    private Long cardBalance;


    private Boolean isNeedWarn;//是否需要提醒

    private String cardTypeName;
}
