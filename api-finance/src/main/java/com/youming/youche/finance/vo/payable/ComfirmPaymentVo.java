package com.youming.youche.finance.vo.payable;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ComfirmPaymentVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/4/8 16:16
 */
@Data
public class ComfirmPaymentVo implements Serializable {

    private String flowId;
    private String desc;
    private Integer chooseResult;
    private String accId;
    private String payAccId;
    private Integer isAutomatic;
    private Integer userType;
    private String fileId;
    private String expireTime;
    private String serviceFee;
}
