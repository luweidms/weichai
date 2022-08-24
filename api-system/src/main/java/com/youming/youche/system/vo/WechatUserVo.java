package com.youming.youche.system.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : [terry]
 * @version : [v1.0]
 * @className : WechatUserVo
 * @description : [用户绑定微信openid]
 * @createTime : [2022/4/13 22:06]
 */
@Data
public class WechatUserVo implements Serializable {


    private static final long serialVersionUID = 8414274155680887751L;

    @NotBlank(message = "openId不能为空")
    private String openId;
    @NotNull(message = "类型不能为空")
    @Min(0)
    @Max(4)
    private Integer appCode;
    @NotNull(message = "phone不能为空")
    private String phone;

    @NotNull(message = "验证码不能为空")
    private String sendCode;

}
