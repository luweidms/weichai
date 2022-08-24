package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName ResetPwdVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/10 10:44
 */
@Data
public class ResetPwdVo implements Serializable {

    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
    @NotBlank(message = "确认密码不能为空")
    private String newPwd2;
}
