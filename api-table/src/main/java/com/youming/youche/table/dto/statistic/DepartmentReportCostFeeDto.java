package com.youming.youche.table.dto.statistic;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentReportCostFeeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String feeName;
    /**
     * 其他费用
     */
    private Long consumeFee;
}
