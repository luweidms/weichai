package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/22
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class SubsidyInfoDto implements Serializable {
    /**
     * 司机姓名
     */
    private String carDriverName;

    /**
     * 司机手机号码
     */
    private String carDriverPhone;

    /**
     * 结算补贴
     */
    private Long salary;

    /**
     * 未结算补贴
     */
    private Long subsidy;
}
