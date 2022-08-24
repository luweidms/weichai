package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RateItem implements Serializable {

    private Long id;

    private Long rateId;

    private Double startValue;

    private Double endValue;

    private Double rateValue;
}
