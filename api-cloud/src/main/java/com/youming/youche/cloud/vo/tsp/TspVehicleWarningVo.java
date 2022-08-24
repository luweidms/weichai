package com.youming.youche.cloud.vo.tsp;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 车联网车辆告警结果对象
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TspVehicleWarningVo extends BaseDomain {

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

	/** 车牌号码 */
	private String carPlate;

	/** 订单号码 */
	private String orderId;

	/** 主驾信息 */
	private String driverName;

	/** 盘点 */
	private String inventory;

	/** 时间 */
	private String costTime;

	/** 位置 */
	private String addr;

	/** 经度 */
	private String lng;

	/** 纬度 */
	private String lat;
}
