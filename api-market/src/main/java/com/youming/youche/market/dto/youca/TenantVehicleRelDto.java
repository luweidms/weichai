package com.youming.youche.market.dto.youca;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 车队与车辆关系表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@Data
public class TenantVehicleRelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人姓名
     */
    private String billReceiverName;


    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 车牌号码，冗余
     */
    private String plateNumber;
    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 车辆类别
     */
    private Integer vehicleClass;


    private LocalDateTime createDate;


}
