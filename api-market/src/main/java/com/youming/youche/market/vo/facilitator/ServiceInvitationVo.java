package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInvitationVo implements Serializable {

    private Long id;
    private Long serviceUserId;
    private String serviceName;

    private Integer serviceType;

    private String serviceTypeName;
    private String linkman;
    /**
     * 站点数量
     */
    private Integer productNum;

    private String createDate;

    /**
     *  '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    private Integer authState;

    private String authStateName;

    //理由
    private String authReason;

    private String loginAcct;
}
