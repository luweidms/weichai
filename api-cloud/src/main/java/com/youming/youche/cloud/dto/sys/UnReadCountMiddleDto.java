package com.youming.youche.cloud.dto.sys;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnReadCountMiddleDto implements Serializable {
    private static final long serialVersionUID = -4536130535456990911L;
    /**
     * 短信类型
     */
    private Integer smsType;
    /**
     * 样式
     */
    private Long cut;

}
