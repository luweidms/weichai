package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class UserRepairInfoOutDto implements Serializable {

    private static final long serialVersionUID = -7396752323300370911L;

    private  Long balance;
    private  Long marginBalance;
    private  Long advanceFee;
    private  Long repairFund;
    private  Long totalAmount ;
    private  Long cash;
    private  String selectType;
    private  Integer payWay;
    private  Long repairId;
    private  String info;





    private String repairCode;
    private String repairDate;
    private String deliveryDate;
    private String productName;
    private String plateNumber;
    private String userName;
    private String userBill;
    private String tenantName;
    private List<RepairItemsWXOutDto> itemList;
    private Long totalFee;
    private String serviceCharge;
    private Long serviceChargeFee;
    private Integer isBill;
    private String isBillName;
    private Integer accountPeriod;//结算周期时间
    private String driverDes;
    private Integer qualityStar;
    private Integer serviceStar;
    private Integer repairState;
    private String repairStateName;
    private Integer priceStar;
    private Long tenantId;

    private Long productId;
    private Long driverUserId;

    private Integer isSure;
    private String isSureName;
    private Long cashFee;


    private List<Map> auditMap;


    private String payWayName;

    private Integer appRepairState;
    private String appRepairStateName;

    private String stateName;

    public Integer getIsSure() {
        return isSure;
    }

    public void setIsSure(Integer isSure) {
        this.isSure = isSure;
    }

    public String IsSureName() {
        if(isSure == 1){
            return "已确认";
        }else if(isSure == 0){
            return "驳回";
        }else{
            return "待确认";
        }
    }

}
