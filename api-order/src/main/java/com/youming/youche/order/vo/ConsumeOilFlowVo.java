package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/4/24
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ConsumeOilFlowVo implements Serializable {

    private static final long serialVersionUID = 274890439520815798L;
    private Long userId;
    //油站id
    private Long productId;
    private String fleetName;
    private String month;
    private String isExpire;
    private Long tenantId;
    /**
     * 用户类型：1-主驾驶 2-副驾驶 3- 经停驾驶 4-车队
     */
    private Integer userType;
    private Integer payUserType;
    private Integer costType;

    private String state;
    private List<String> stateList;
    private List<Long> tenantIds;

    //流水号
    private Long flowId;
    private String flowIds;

    // APP接口-优惠加油-加油记录-评价 入参
    private Integer evaluatePrice;
    /**
     * 评价质量
     */
    private Integer evaluateQuality;
    /**
     * 评价服务
     */
    private  Integer evaluateService;

    //APP接口-优惠加油-确认支付 入参
    private Long oilPrice;
    private String oilRiseTemp;
    private String payPasswd;
    private String payCode;
    private Integer isNeedBill;
    /**
     * 是否现场价加油 0不是，1是
     */
    private String localeBalanceState;
    private Long amountFee;
    /**
     * 车牌号
     */
    private String plateNumber;

    //APP接口-预支-查询预支手续费
    private  Long advanceAmount;

    // 找油网加油_支付
    private  Long id;

    // 油账户列表油站详情
    private  Long amount;
    private String latitude;
    private String longitude;
    private Boolean isShare;
}
