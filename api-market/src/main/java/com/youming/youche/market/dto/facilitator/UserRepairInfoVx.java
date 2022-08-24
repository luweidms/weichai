package com.youming.youche.market.dto.facilitator;

import com.youming.youche.market.vo.facilitator.RepairItemsVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserRepairInfoVx  implements Serializable {

    private static final long serialVersionUID = 5690609037652904436L;

    /**
     * 站点编号
     */
    private Long productId;

    /**
     * 司机id
     */
    private Long driverUserId;

    /**
     *维修时间
     */
    private String repairDate;

    /**
     * 交付时间
     */
    private String deliveryDate;
    /**
     * 司机手机
     */
    private String userBill;
    /**
     * 司机姓名
     */
    private String userName;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 金额
     */
    private String totalFee;

    /**
     * 是否开票（1.是、2.否）
     */
    private Integer isBill;


    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 车辆类别
     */
    private String vehicleClass;

    /**
     * 维修单id
     */
    private Long repairId;

    /**
     * 维修类型（状态1保养费2维修费'）
     */
    private Long tenanceType;

    private List<RepairItemsVo> rateItemList;
}
