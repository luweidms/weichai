package com.youming.youche.finance.vo.ac;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询司机工资入参
 *
 * @author zengwen
 * @date 2022/4/18 14:44
 */
@Data
public class CmSalaryInfoNewQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结算月份
     */
    private String settleMonth;

    /**
     * 司机姓名
     */
    private String carDriverName;

    /**
     * 司机手机
     */
    private String carDriverPhone;

    /**
     * 工资申诉状态
     */
    private Integer salaryComplainSts;

    /**
     * 核销开始时间
     */
    private String startTime;

    /**
     * 核销结束时间
     */
    private String endTime;

    /**
     *
     */
    private Integer state;

    /**
     *
     */
    private Integer userType;

}
