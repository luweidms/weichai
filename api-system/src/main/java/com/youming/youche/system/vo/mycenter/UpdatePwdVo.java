package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName updatePwdVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/16 18:16
 */
@Data
public class UpdatePwdVo implements Serializable {

    @NotNull(message = "用户Id不能为空")
    private Long userId;
    @NotBlank(message = "登录帐号名称不能为空")
    private String loginAccount;
    @NotBlank(message = "原密码不能为空")
    private String oldPwd;
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
    @NotBlank(message = "确认密码不能为空")
    private String newPwd2;

}
