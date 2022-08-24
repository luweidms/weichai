package com.youming.youche.system.dto.mycenter;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/4/25
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class BankFlowDetailsAppDto implements Serializable {
    private String month;
    private List<BankFlowDetailsAppOutDto> out;
}
