package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/18 19:26
 */
@Data
public class GetOilCardByCardNumVo implements Serializable {

    /**
     * 油卡卡号
     */
    private String oilCarNum;

    /**
     * 状态
     */
    private Integer oilCardStatus;

    /**
     *
     */
    private Integer orderByOilCardNum;

    /**
     * 服务商
     */
    private String companyName;

    /**
     * 车牌号
     */
    private String bindVehicle;

    /**
     * 油卡类型
     */
    private Integer oilCardType;

    /**
     *
     */
    private Integer cardType;

    /**
     * 租户ID
     */
    private Long tenantId;

}
