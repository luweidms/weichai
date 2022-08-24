package com.youming.youche.order.dto;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 车联网车辆轨迹查询结果对象
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@Data
public class TspVehicleTrackDto implements Serializable {

	private static final long serialVersionUID = 1L;

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

	/** 位置 */
	private String addr;

	/** 经度 */
	private String lng;

	/** 纬度 */
	private String lat;


}
