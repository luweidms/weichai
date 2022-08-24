package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName UpdatePhoneVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/7 20:54
 */
@Data
public class UpdatePhoneVo implements Serializable {

    @NotBlank(message = "原手机号不能为空")
    private String oldPhone;
    @NotBlank(message = "新手机号不能为空")
    private String newPhone;
    @NotBlank(message = "手机验证码不能为空")
    private String verifyCode;
}
