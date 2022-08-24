package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/4/2 18:04
 */
@Data
public class OrderConsumeOilDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalConsumeAmount;//总油费 单位分
    private Double totalConsumeRise;//总油费升数 单位升
}
