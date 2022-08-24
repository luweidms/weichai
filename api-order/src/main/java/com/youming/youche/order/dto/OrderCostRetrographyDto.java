package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzx
 * @date 2022/4/2 20:07
 */
@Data
public class OrderCostRetrographyDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalMileage;

    private Long totalOil;

    private List<OrderCostInfoDto> costInfoOuts;

    private Integer orderCount;

}
