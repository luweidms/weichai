package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRepairMarginDto implements Serializable {

    private static final long serialVersionUID = -7949668849486483569L;
    private  Long advanceFee;//预支手续费
    private  Long marginBalance;// 即将到期余额
    private  Long balance;// 可提现余额
    private  Long repairBalance;// 维修基金余额 --使用维修基金金额(分)
    private  Long sumAmount;//即将到期余额=使用未到期金额(分)-预支手续费(分)

}
