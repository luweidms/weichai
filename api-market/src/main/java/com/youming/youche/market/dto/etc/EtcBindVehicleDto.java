package com.youming.youche.market.dto.etc;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @ClassName EtcBindVehicleDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/23 13:59
 */
@Data
public class EtcBindVehicleDto implements Serializable {

   private String vehicleCode;
   private String plateNumber;
   private Long driverUserId;
   private String linkman;
   private String mobilePhone;

}
