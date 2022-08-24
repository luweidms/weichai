package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BankCardListVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/26 17:23
 */
@Data
public class BankCardListVo implements Serializable {
    /** id */
    private Long id;
    /** 账户Id */
    private Long acctId;
    /** 账户名称 */
    private String acctName;
    /** 开户行 */
    private String bankName;
    /** 银行卡号 */
    private String acctNo;
    private String bankType;
}
