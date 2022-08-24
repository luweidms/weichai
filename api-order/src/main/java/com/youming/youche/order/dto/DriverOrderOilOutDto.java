package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DriverOrderOilOutDto implements Serializable {


    private static final long serialVersionUID = -3217892806063148219L;

    private Long userId;//用户id
    private Long orderOil;//订单油
}
