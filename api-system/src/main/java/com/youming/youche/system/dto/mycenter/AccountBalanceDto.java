package com.youming.youche.system.dto.mycenter;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName AccountBalanceDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/22 18:32
 */
@Data
public class AccountBalanceDto implements Serializable {

    /** 证照类型 */
    private String certType;
    /** 余额 */
    private BigDecimal balance;
}
