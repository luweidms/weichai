package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName UpdatePayPwdVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/21 9:00
 */
@Data
public class SetPayPwdVo implements Serializable {
    @NotNull(message = "用户Id不能为空")
    private Long userId;
    @NotBlank(message = "用户登录帐号不能为空")
    private String phone;
    @NotBlank(message = "密码不能为空")
    private String pwd;
    @NotBlank(message = "确认密码不能为空")
    private String pwd2;
}
