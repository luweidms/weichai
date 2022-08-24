package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilAccountOutDto  implements Serializable {

    /*
     * 用户id
     */
    private Long userId;
    /*
     * 油费金额
     */
    private Long amount;
    /*
     * 油费消费对象：1自有，2共享
     */
    private Integer oilConsumer;
    /*
     * 油费金额归属租户id
     */
    private Long tenantId;
    /*
     * 备注
     */
    private String remark;
}
