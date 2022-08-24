package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoQueryDto implements Serializable {
    private String codeName; // 类型名称
    private String codeValue;  // 类型
    private String updateShow; //是否显示
}
