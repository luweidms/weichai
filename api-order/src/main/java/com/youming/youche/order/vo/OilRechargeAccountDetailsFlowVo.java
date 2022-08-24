package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OilRechargeAccountDetailsFlowVo  implements Serializable {

    private static final long serialVersionUID = -2662218488805761267L;
    private  String startTime;
    private String endTime;
    private String acctNameOrNo;
    private String busiType;
    private Integer type;
    private  String busiCode;
    private String orderNum;
    /**
     * 车牌号
     */
    private String plateNumber;
    private String relationName;
    private String relationPhone;
    private Integer oilRechargeBillType;
    /**
     * 用户id
     */
    private  Long userId;
    private List<String> payAccIds;
    private  List<Long>  accIds;
    private  List<Long> userIds;
    private  List<String> busiCodes;
    private  List<Long> orders;
    private   Integer[] sourTypes;
    private LocalDateTime startTime1;
    private LocalDateTime endTime1;
    private  List<String>  busiTypeStr;
}
