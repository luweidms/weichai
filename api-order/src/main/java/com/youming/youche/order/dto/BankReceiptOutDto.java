package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BankReceiptOutDto  implements Serializable {

    private static final long serialVersionUID = -2559455468090182875L;
        private String url;//网址
        private String bankPreFlowNumber;//电子回单号(银行前置流水号)
        private String code;//验证码

}
