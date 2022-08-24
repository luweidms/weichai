package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderAccountVO implements Serializable {

    private Long userId;
    private Integer state;
    /**
     * 备注
     */
    private String remark;
    private Long sourceTenantId;
    /**
     * 用户类型：1-主驾驶 2-副驾驶 3- 经停驾驶 4-车队
     */
    private Long userType;
}
