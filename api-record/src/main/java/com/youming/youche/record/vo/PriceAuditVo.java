package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/6/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class PriceAuditVo implements Serializable {

    private String busiCode;
    private Long busiId;
    private String desc;
    private Integer chooseResult;
}
