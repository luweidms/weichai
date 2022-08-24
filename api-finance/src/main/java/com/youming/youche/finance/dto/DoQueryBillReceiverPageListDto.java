package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/22 13:22
 */
@Data
public class DoQueryBillReceiverPageListDto implements Serializable {

    private String billReceiverMobile;
    private String billReceiverUserId;
    private String billReceiverName;
    private Integer vehicleClass;
    private Integer userType;
    private Long vehicleNum;
}
