package com.youming.youche.market.dto.etc;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName EtcMaintainDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/19 18:13
 */
@Data
public class EtcMaintainDto implements Serializable {

    private Long id;
    private String etcId;//ETC卡号
    private Integer etcCardType;//ETC卡类型 1粤通卡 2鲁通卡 3赣通卡
    private String serviceName;//服务商
    private Long userId;//供应商用户ID
    private Integer paymentType;//'付费类型 1预付费 2后付费
    private String productName;//站点名称
    private String accountPeriodRemark;//账期
    private String bindVehicle;//现绑定的车辆
    private Integer state;//状态 0，无效 1，有效
    private Long service_name_id;//服务商id
    private String ReasonFailure; // 失敗原因
    private  Long vehicleCode;// 车辆编号
}
