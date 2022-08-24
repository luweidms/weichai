package com.youming.youche.cloud.dto.tsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 车联网车辆告警查询参数对象
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@Data
public class TspVehicleWarningDto  implements Serializable {
	private static final long serialVersionUID = 1L;

	/** ⻋架号 */
	private String vin;

	/** 起始时间 */
	private String startTime;

	/** 终⽌时间 */
	private String endTime;

	/** ⻋报警类型 */
	private String alarmTypes;

	/** 过滤停⻋ */
	private String filter0;

	/** 过滤⽆效定位 */
	private String filterInvalid;

	public TspVehicleWarningDto(String vin, String startTime, String endTime) {
		this.vin = vin;
		this.startTime = startTime;
		this.endTime = endTime;
		this.alarmTypes = "";
		this.filter0 = "0";
		this.filterInvalid = "0";
	}
}
