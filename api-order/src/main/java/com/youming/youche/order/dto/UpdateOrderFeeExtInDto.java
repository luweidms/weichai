package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateOrderFeeExtInDto  implements Serializable {
    private Long salary;
    private Long estFee;
    private Long copilotSalary;
    private Long pontage;
    private Long driverDaySalary;
    private Long copilotDaySalary;
    private Integer subsidyDay;
    private Integer subsidyCopilotDay;
    private Long monthSalary;
    private Long monthSubSalary;
    private String subsidyTime;
    private String copilotSubsidyTime;
    private Integer salaryPattern;
    private Integer copilotSalaryPattern;
    private Long oilLitreTotal;
    private Long oilLitreVirtual;
    private Long oilLitreEntity;
    private Long oilPrice;
    /**
     * 路桥费的单价
     */
    private Long pontagePer;
    private Integer oilConsumer;
    private Integer oilAccountType;
    private Integer oilBillType;
}
