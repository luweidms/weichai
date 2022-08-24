package com.youming.youche.system.dto.ac;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/24 13:26
 */
@Data
public class AccountDetailsWXDto implements Serializable {

    private Long userId;

    private List<OrderAccountOutDto> items;
}
