package com.youming.youche.finance.dto.ac;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/29 16:09
 */
@Data
public class CmSalaryInfoQueryDto implements Serializable {

    /**
     * 工资单ID
     */
    private Long salaryId;

    /**
     * 月份
     */
    private String settleMonth;

    /**
     * 司机ID
     */
    private Long carDriverId;

    /**
     * 司机名称
     */
    private String carDriverMan;

    /**
     * 司机手机
     */
    private String carDriverPhone;

    /**
     * 已结算金额
     */
    private Long paidSalaryFee;

    private Double paidSalaryFeeDouble;

    /**
     * 节油奖
     */
    private Long saveOilBonus;

    private Double saveOilBonusDouble;

    /**
     * 基本工资
     */
    private Long basicSalaryFee;

    private Double basicSalaryFeeDouble;

    /**
     * 自定义列
     */
    private List<CmSalaryInfoExtDto> cmSalaryInfoExts;

    /**
     * 时间
     */
    private LocalDateTime createTime;

    /**
     * 补贴金额信息
     */
    List<CmSalarySendInfoDto> cmSalarySendInfoDtos;

    /**
     * 申诉数量
     */
    private Long complainCount;

    /**
     * 工资单状态
     */
    private Integer state;

    private String stateName;
}
