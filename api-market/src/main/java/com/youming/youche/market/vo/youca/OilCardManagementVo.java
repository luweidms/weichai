package com.youming.youche.market.vo.youca;

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
public class OilCardManagementVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String oilCarNum;
    private  Long userId;
    private String companyName;
    private Integer oilCardStatus;
    private Integer cardType;
    private Integer oilCardType;
    private String bindVehicle; // 绑定车辆

    private Integer orderByOilCardNum;

    private  Long CardBalance;// 理论余额
}
