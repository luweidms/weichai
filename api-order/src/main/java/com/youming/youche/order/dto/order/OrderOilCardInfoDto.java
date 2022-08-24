package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/7/20 16:43
 */
@Data
public class OrderOilCardInfoDto implements Serializable {

    private Integer cardType;

    private String oilCardNum;

    private Long oilFee;
}
