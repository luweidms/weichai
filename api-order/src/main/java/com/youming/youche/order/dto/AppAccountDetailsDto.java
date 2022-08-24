package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/10
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AppAccountDetailsDto implements Serializable {
    private static final long serialVersionUID = 4973230164223847225L;
    private List<AppAccountDetailsOutDto> items;
    private String month;//月份
}
