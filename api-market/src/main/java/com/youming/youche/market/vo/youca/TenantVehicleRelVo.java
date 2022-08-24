package com.youming.youche.market.vo.youca;

import lombok.Data;

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
public class TenantVehicleRelVo implements Serializable {
    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;
    /**
     * 账单接收人姓名
     */
    private String billReceiverName;
    /**
     * 车牌号码，冗余
     */
    private String plateNumber;





}
