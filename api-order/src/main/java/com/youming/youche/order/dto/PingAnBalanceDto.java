package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PingAnBalanceDto  implements Serializable {


    private static final long serialVersionUID = -2281063480179094173L;

    private Long userId;
    //WX接口-查询可转移金额 返回值
    private Long privateReceivableAccount;//私人收款账户余额
    private Long privatePayableAccount;//私人付款账户余额
    private Long businessReceivableAccount;//对公收款账户余额
    private Long businessPayableAccount;//对公付款账户余额

    // 微信接口-提现界面 返回值
    private Long incomeBalance1;//对公收款账户余额(分)
    private Long incomeBalance2; //对私收款账户余额(分)
    private Long payBalance1;//对公付款账户余额(分)
    private Long payBalance2;//对私付款账户余额(分)

    private  String flag;//状态

    private  String  accNoStr;
    private  Long  amount;

    private  Long canAdvance; // 可预支余额

    private  Long serviceCharge; //根据预支金额获取预支手续费
}
