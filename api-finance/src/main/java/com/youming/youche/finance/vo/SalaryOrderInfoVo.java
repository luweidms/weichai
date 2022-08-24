package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SalaryOrderInfoVo implements Serializable {
    /**
     * 公里数
     */
    private Long formerMileage;
    /**
     * 本趟工资
     */
    private Long tripSalaryFee;
    /**
     * 本趟工资
     */
    private Double tripSalaryFeeDouble;

    /**
     * 节油奖
     */
    private Long saveOilBonus;


    /**
     * 节油奖
     */
    private Double saveOilBonusDouble;



    /**
     * 公里数
     */
    private Double mieageNumberStr;


    private String c;
}
