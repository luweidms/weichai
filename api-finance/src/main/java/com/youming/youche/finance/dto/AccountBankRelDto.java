package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 借支新增  收款人返回值
 */
@Data
public class AccountBankRelDto implements Serializable {

    private static final long serialVersionUID = 6795162528529780557L;
    /**
     * 银行账户
     */
    private String acctNo;


    /**
     * 账户名称
     */
    private String acctName;


    private Integer authState;


    /**
     * 开户银行
     */
    private String bankName;


    /**
     * 收款账户类型：0私人账户；1对公账户
     */
    private Integer bankType;

    private String pinganCollectAcctId;//最终收款账户虚拟卡号

    /**
     * 联系人手机
     */
    private String mobilePhone;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    private String carDriverPhone;//司机手机

    private String collectAcctId;

    private String bankTypeName;

}
