package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CheckLineIsOkByPlateNumberVo implements Serializable {

    private static final long serialVersionUID = 6606309900115957071L;

    private Long orderId;
    private LocalDateTime arriveDate;
}
