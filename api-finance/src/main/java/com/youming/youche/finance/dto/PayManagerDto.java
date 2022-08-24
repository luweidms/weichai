package com.youming.youche.finance.dto;

import com.youming.youche.system.domain.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PayManagerDto implements Serializable {
    private String state; //状态 （0待审核、1审核不通过、2付款中、3已付款）
    private String payAmt; //付款金额
    private String isNeedBill; //是否开票 0否 1是
    private String receBankNo; //收款方银行卡号
    private String receBankName; //收款方银行
    private String receName; //收款方名称
    private String receUserName; //收款账户名
    private String receBranchName; // 收款银行名称
    private List<SysOperLog> sysOperLogs; //日志
    private List<Long> fileId; //附件ID
    private List<String> fileUrl; //附件地址
}
