package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 邀请我的 成功or失败 入参
 */
@Data
public class ApplyRecordSuccessOrFailVo implements Serializable {

    private static final long serialVersionUID = -2541632314344027792L;

    private Long applyId;
    private String dec;

}
