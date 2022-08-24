package com.youming.youche.system.dto.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/25
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class BankFlowDetailsAppOutDto extends BankFlowDetailsOutDto implements Serializable {
    private String acctNoName;//账户
    private String acctNameInType;
    private String acctNameOutType;
}
