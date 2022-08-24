package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/26
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class BankFlowDownVo implements Serializable {
    private Long userId;
    private String beginDate;
    private String endDate;
    private String acctIdIn;
    private String acctIdOut;

}
