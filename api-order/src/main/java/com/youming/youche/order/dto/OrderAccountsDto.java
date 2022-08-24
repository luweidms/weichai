package com.youming.youche.order.dto;

import com.youming.youche.system.dto.ac.OrderAccountOutDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderAccountsDto implements Serializable {
    private List<OrderAccountOutDto> items;
    private Long userId;
}
