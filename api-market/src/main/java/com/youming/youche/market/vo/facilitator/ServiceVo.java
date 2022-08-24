package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ServiceVo  implements Serializable {
    /**
     * 流水号 业务单号
     */
    private String serialData;

    private String serviceName;
    /**
     * 收款方手机号
     */
    private String mobile;
    /**
     * 车牌号码
     */
    private String plateNumber;

    private String driverName;

    private String typeName;

    private double amount;

    private LocalDateTime time;

}
