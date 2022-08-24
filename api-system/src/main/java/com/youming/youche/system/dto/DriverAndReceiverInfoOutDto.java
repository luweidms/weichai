package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DriverAndReceiverInfoOutDto implements Serializable {
    private Long userId;
    private String linkman;
    private String mobilePhone;
    private Long  receiverId;
    private String receiverName;
}
