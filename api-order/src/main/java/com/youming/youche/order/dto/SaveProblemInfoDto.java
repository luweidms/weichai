package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SaveProblemInfoDto implements Serializable {

    private static final long serialVersionUID = 54866907448394062L;

    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 异常ID
     */
    private Long problemId;
    /**
     * 异常类型
     */
    private Integer problemType;
    /**
     * 责任司机id
     */
    private Long carOwnerId;
    /**
     * 责任司机名称
     */
    private String carOwnerName;
    /**
     * 责任司机手机号
     */
    private String carOwnerPhone;
    /**
     * 责任方
     */
    private Integer problemCondition;
    /**
     * 异常金额
     */
    private String problemPrice;
    /**
     * 异常描述
     */
    private String problemDesc;
    /**
     * 责任方名称
     */
    private String responsiblePartyName;
}
