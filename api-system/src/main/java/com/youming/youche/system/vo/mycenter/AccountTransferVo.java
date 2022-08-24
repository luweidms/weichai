package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName AccountTransferVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 16:46
 */
@Data
public class AccountTransferVo implements Serializable {

    @NotNull(message = "付款用户Id不能为空")
    private Long userId;
    @NotBlank(message = "支付密码不能空")
    private String payPwd;
    @NotNull(message = "付款账户Id不能为空")
    private Long payAccountId;
    @NotNull(message = "收款账户Id不能为空")
    private Long recvAccountId;
    @NotBlank(message = "付款方子商户编号不能为空")
    private String mbrNo;
    @NotBlank(message = "付款方子商户名称不能为空")
    private String mbrName;
    @NotBlank(message = "转帐金额不能为空")
    private String tranAmt;
    @NotBlank(message = "收款方子商户编号不能为空")
    private String recvMbrNo;
    @NotBlank(message = "收款方子商户名称不能为空")
    private String recvMbrName;
    private String remark;
    private Long payoutId;
}
