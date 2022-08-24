package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/9
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class LineInfoVo implements Serializable {
    private String sourceName;
    private String beginTime;
    private String endTime;

}
