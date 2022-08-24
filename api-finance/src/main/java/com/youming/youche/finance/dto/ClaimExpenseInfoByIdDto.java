package com.youming.youche.finance.dto;

import com.youming.youche.finance.domain.ClaimExpenseInfo;
import com.youming.youche.system.domain.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClaimExpenseInfoByIdDto implements Serializable {

    private static final long serialVersionUID = -5894862983039305045L;

    private ClaimExpenseInfo claimExpenseInfo;

    private List<SysOperLog> sysOperLogs;

}
