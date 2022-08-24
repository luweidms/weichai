package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author: luona
 * @date: 2022/5/14
 * @description: T0DO
 * @version: 1.0
 */

@Data
public class OrderListWxVo implements Serializable {
    private static final long serialVersionUID = 8718162936948882894L;
    private Boolean isHis;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 到达市
     */
    private Integer desRegion;

    /**
     * 订单状态0:待确认、1：正在交易、2：已确认、3：在运输、4完成
     */
    private String orderStates;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 租户ID
     */
    private Long tenantId;
    private Long collectionUserId;//代收人id
    private Long collectionTenantId;//代收车队
    private Integer selectOrderType;
    /**
     * 车牌号
     */
    private String plateNumber;
    private Boolean todo;//审核权限
    private List<Long> busiId;//业务ID集合
    private Boolean hasAllData;//是否有所有权限
    private List<Long> orgIdList;//部门集合
    private String customNumber;//客户单号或回单号


    private String orderState;
//    private String orderSatetTwo;
//    private String orderUpdateState;
//    private String orderStateOne;
//    private String orderStateTwo;
    private String[] cutomNumbers;
    private Set<String> reciveNumber;

    private String[] arr;


    private  List<String> arrs;
}
