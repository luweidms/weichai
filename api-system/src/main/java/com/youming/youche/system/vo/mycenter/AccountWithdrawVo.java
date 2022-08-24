package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName AccountWithdrawVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 16:47
 */
@Data
public class AccountWithdrawVo implements Serializable {

    @NotNull(message = "用户Id不能为空")
    private Long userId;
    @NotNull(message = "账户Id不能为空")
    private Long accountId;
    @NotBlank(message = "支付密码不能空")
    private String payPwd;
    @NotBlank(message = "账户编号不能空")
    private String mbrNo;
    @NotBlank(message = "提现金额不能为空")
    private String tranAmt;
    @NotBlank(message = "银行帐号不能为空")
    private String accNo;
    @NotBlank(message = "银行户名不能为空")
    private String accName;
    @NotBlank(message = "开户行名称不能为空")
    private String eab;
    private String remark;
}
