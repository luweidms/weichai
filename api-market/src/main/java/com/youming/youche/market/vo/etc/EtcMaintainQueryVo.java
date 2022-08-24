package com.youming.youche.market.vo.etc;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName EtcMaintainQueryVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/19 18:03
 */
@Data
public class EtcMaintainQueryVo implements Serializable {
    private Integer state; //卡类型
    private String etcId; //ETC卡号
    private String serviceName; //服务商
    private String bindVehicle; //捆绑车俩
    private Integer paymentType; //付费类型
    private Integer etcCardType; //ETC状态类型
    private String productName; //站点名称
    private Long tenantId; //租户id

}
