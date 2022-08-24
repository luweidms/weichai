package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hzx
 * @date 2022/3/22 19:44
 */
@Data
public class VehicleCertInfoDto implements Serializable {

    private static final long serialVersionUID = -1L;

    private Date prevMaintainTime;//上次保养时间
    private Date insuranceTime;//上次保险时间
    private Date seasonalVeriTime;//上次季审时间
    private Date annualVeriTime;//上次年审时间
    private Integer maintainDis;//保养周期
    private Integer maintainWarnDis;//保养预警公里数
    private Date vehicleValidityTime;//行驶证有效期
    private Date operateValidityTime;//运营证有效期
    private Date annualVeriTimeEnd;//年审失效时间
    private Date seasonalVeriTimeEnd;//季审失效时间
    private Date busiInsuranceTime;//商业险超始时间
    private Date busiInsuranceTimeEnd;//商业险失效时间
    private String busiInsuranceCode;//商业保险单号
    private Date insuranceTimeEnd;//交强险失效时间
    private Date otherInsuranceTime;//其他险超始时间
    private Date otherInsuranceTimeEnd;//其他失效时间
    private String otherInsuranceCode;//其他保险单号
    private Date vehicleValidityTimeBegin;//行驶证有效期超始时间
    private Date operateValidityTimeBegin;//营运证有效期超始时间

}
