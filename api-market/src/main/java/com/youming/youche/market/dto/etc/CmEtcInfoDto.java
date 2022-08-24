package com.youming.youche.market.dto.etc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.util.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CmEtcInfoDto  implements Serializable {

    private static final long serialVersionUID = 4616207189154048942L;
    /**
     * 靠台时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime car_depend_date;
    private  Long id ; // etc 消费记录 id
    private Long orderId; // 订单号
    private String user_name;// 客户简称
    private String plate_number;// 车牌号
    private String contact_number; //司机手机号
    private String linkman;//司机姓名
    private String vehicle_length;//车型
    private String oilAffiliation; //线路名称
    private  Integer payment_way;// 成本模式
    private  Long consume_money;// 消费金额
    private String customName;
    private String sourceName;
    private String collectMobile;
    private  String collectName;
    private   LocalDateTime carDependDate;
    private  String consumeMoney;
    private  String serviceProviderName;
    private  Integer etcUserType;
    private  Integer paymentWay;
    private  Long tenantId;
    private  String plateNumber;
    private  Long serviceProviderId; // 服务商id
    /**
     * 卡号
     */
    private String etcCardNo;

    private String vehicleInfo;

    private String etcUserTypeName;
    private  String paymentWayName;   //成本模式

    private  String ReasonFailure;// 失败原因

    // 车辆类型 类型
    private String vehicleStatusName;
    // 车辆类型
    private String vehicleTypeName;   // 车辆类型 车长
    private String vehicleLengthName;


    public String getVehicleTypeName() {
        vehicleTypeName = "";
        if (StringUtil.isNotEmpty(vehicleStatusName) || StringUtil.isNotEmpty(vehicleLengthName)) {
            vehicleTypeName = vehicleStatusName + "/" + vehicleLengthName;
        }
        return vehicleTypeName;
    }


}
