package com.youming.youche.system.dto.mycenter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: luona
 * @date: 2022/4/25
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class BankFlowDetailsOutDto implements Serializable {
    private long flowId;
    private String businessType;//业务类型
    private String businessName;//业务类型名称
    private Date tranDate;//交易日期
    private Long userId;//用户id
    private String acctNameOut;//转出账户名
    private String acctNoOut;//转出账号
    private String acctNameIn;//转入账户名
    private String acctNoIn;//转入账号
    private String tranAmountStr;//交易金额(元)
    private Long tranAmount;//交易金额(分)
    private Integer tranState;//状态
    private String tranStateName;
    private String bankPreFlowNumber;//银行前置流水号
    private String tranFlowNumber;//交易网流水号
    private String verificationCode;//回单验证码
    private String receiptCode;//回单图片ID
    private String receiptUrl;//回单图片URL
    private String receiptUrlDown;//打包用
    private Integer flowType;
    private Long userIdOut;
    private Long userIdIn;

}
