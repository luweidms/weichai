package com.youming.youche.cloud.dto.tsp;

import lombok.Data;

/**
 * 车联网车辆信息跨服务调用Dto对象
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@Data
public class TspVehicleInfoDto {
	/** 客户名称 */
	private String companyName;

	/** ⻋架号 */
	private String vin;

	/** 客户线路 */
	private String customerLineName;

	/** 车辆类别 */
	private String vehicleClassName;

	/** 车牌号码 */
	private String plateNumber;

	/** 订单号码 */
	private String orderId;

	/** 主驾信息 */
	private String driverName;
}
