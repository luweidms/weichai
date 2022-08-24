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
public class BankFlowDownloadUrlVo implements Serializable {
    private String downloadUrl;
    private BankFlowDownVo bankFlowDownVo;
}
