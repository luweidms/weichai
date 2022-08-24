package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class OtherDataDto implements Serializable {


    private static final long serialVersionUID = -8031477118622064090L;

    /**
     * 操作员组织
     */
    private Long opOrgId;

    /**
     * 回单状态
     */
    private Integer reciveState;

    /**
     * 合同状态
     */
    private Integer loadState;

    /**
     * 车辆归属枚举(对应 静态数据字段  capital_channel)
     */
    private String vehicleAffiliation;

    /**
     * 尾款状态
     */
    private Integer finalFeeFlag;


    /**
     * 已打款状态
     */
    private Integer payoutStatus;

    /**
     * 1待核销；2已核销；3已撤销
     */
    private Integer verificationState;

    /**
     * 估算价格状态0:等待2:失败1:成功
     */
    private Integer preAmountFlag;

    /**
     * 主驾驶月工资
     */
    private String monthSalary;

    /**
     * 主驾驶工资模式：1普通，2里程，3按趟
     */
    private Integer salaryPattern;

    /**
     * 司机每天补贴金额
     */
    private String driverDaySalary;
}
