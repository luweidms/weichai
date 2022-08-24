package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AuthInfoReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:31
 */
@Data
public class AuthInfoReqDto implements Serializable {
    /** 验证方式（固定为1）：
     1：手机号 */
    private String authType;

    /** 银行验证流水，同4.1返回接口中的respNo */
    private String origAuthRespNo;

    /** 验证码 */
    private String verifyCode;
}
