package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/4/21 14:22
 */
@Data
public class UpdateUserDataInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    long userId; // 用户编号
    String userName; // 用户名称
    String receiverName; // 收款人名称
    String idCard; // 身份证号

}
