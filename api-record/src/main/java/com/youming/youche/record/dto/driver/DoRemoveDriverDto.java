package com.youming.youche.record.dto.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoRemoveDriverDto
 * @Package: com.youming.youche.record.dto.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/26 18:41
 * @company:
 */
@Data
public class DoRemoveDriverDto implements Serializable {

   private Long tenantUserRelId; // 用户id

   private String tenantVehicleIds; // 车辆编号
}
