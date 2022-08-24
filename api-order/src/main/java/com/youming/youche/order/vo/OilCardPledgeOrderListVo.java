package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/5/20 10:18
 */
@Data
public class OilCardPledgeOrderListVo implements Serializable {

    private Long orderId;
    private List<Long> orderIds;
    private String plateNumber;
    private String oilCarNum;
    private String carDriverMan;
    private String carDriverPhone;
    private String customOrderId;
    private Integer pageNum;
    private Integer pageSize;
}
