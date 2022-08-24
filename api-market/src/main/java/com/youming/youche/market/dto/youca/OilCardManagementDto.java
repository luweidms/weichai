package com.youming.youche.market.dto.youca;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 油卡管理表
 * </p>
 *
 * @author Terry
 * @since 2022-02-09
 */
@Data
public class OilCardManagementDto implements Serializable {



    private Long id;
    private String oilCarNum;
    private String companyName;
    private Long serviceUserId;
    private Integer oilCardStatus;
    private Long pledgeOrderId;
    private Long productId;
    private Integer serviceType;
    private Integer oilCardType;
    private Integer servState;
    private Long userId;
    private Integer cardType;
    private Long cardBalance;
    private String cardBalaceStr;
    private String bindVehicle; // 现绑定车辆
    private String oilCardStatusName;
    private String oilCardTypeName;

    /**
     * (导入失败原因)
     */
    private String reasonFailure;
    /**
     * 卡名称
     */
    private  String cardTypeName;
}
