package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/10
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class SubjectsOutDto implements Serializable {
    private Long subjectsId;//科目id
    private String subjectsName;//科目名称
    private Long subjectsAmount;//科目金额
    private Integer subCostType;//费用类型 1：支出 2：收入3：其他
}
