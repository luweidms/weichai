package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDataLinkManDto implements Serializable {

    private static final long serialVersionUID = 8016576575217269115L;
    /**
     * 租户联系人
     */
    private String linkMan;


    private Long userId;

    private String mobilPhone;

    private Integer carUserType;

    private Long backhaulGuideUserId;

    private String  backhaulGuideName;

    private String backhaulGuidePhone;

    private String carUserTypeName;
}
