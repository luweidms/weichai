package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/13
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OaloanCountDto implements Serializable {
    private int count;//数量
    private Long amount;//费用
}
