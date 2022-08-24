package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class OrderListAppVo implements Serializable {

    private static final long serialVersionUID = 1289025215652247848L;

    private Boolean isHis;
    private Long orderId;
    private Integer desRegion;
    private String orderStates;
    private String reciveState;
    private Integer sourceRegion;
    private String tenantName;
    private Long carDriverId;
    private Integer orderSelectType;
    private String customNumber;//客户单号或回单号

    private String[] reciveStateArr;
    private String[] orderStatesArr;
    private Set<String> reciveNumber;
    private Long tenantId;
    /**
     * 司机账号
     */
    private String carDriverPhone;


}
