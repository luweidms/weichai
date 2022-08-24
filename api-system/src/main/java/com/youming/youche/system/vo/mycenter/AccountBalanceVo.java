package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserAccountBalanceVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/22 16:06
 */
@Data
public class AccountBalanceVo implements Serializable {

    /**
     * 总账户余额
     */
    private String totalAccBalance;
    /**
     * 公户总余额
     */
    private String publicAccBalance;
    /**
     * 私户总余额
     */
    private String privateAccBalance;
}
