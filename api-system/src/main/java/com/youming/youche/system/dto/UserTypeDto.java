package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserTypeDto implements Serializable {
    private boolean adminUser;
    private boolean staff;
    private boolean driver;
    private boolean service;
    private boolean receiver;

}
