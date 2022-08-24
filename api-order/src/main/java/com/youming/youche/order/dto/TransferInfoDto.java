package com.youming.youche.order.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-14
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    @TableName("order_transfer_info")
    public class TransferInfoDto extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 车队名称
     */
    private String transferTenantName;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 原订单号
     */
    private Long orderId;

    /**
     * 新订单号
     */
    private Long transferOrderId;

    /**
     * 转单状态 transferOrderState 转单状态(0待接单 1 已接单 2 已拒接 3 已超时)
     */
    private Integer transferOrderState;

    /**
     * 转单时间起止时间
     */
    private String beginTranDate;

    /**
     * 转单时间终止时间
     */
    private String endTranDate;

    /**
     * 接单时间起止时间
     */
    private String beginAcceptDate;

    /**
     * 接单时间终止时间
     */
    private String endAcceptDate;

    private Integer pageNum;
    private Integer pageSize;


}
