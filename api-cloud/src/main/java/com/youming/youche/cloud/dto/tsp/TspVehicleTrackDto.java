package com.youming.youche.cloud.dto.tsp;

import lombok.Data;

import java.io.Serializable;

/**
 * 车联网车辆轨迹查询参数对象
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@Data
public class TspVehicleTrackDto  implements Serializable {
	private static final long serialVersionUID = 1L;


	/** ⻋架号 */
	private String vin;

	/** 起始时间 */
	private String startTime;

	/** 终⽌时间 */
	private String endTime;

	/** 查询停⻋ */
	private String queryStop;

	/** 查询报警 */
	private String queryAlarm;

	/** 过滤⽆效定位 */
	private String filterInvalid;

	/** 查询超速路段 */
	private String queryOverspeed;

	/** 车牌号 */
	private String plateNum;



	public TspVehicleTrackDto(String plateNum,String startTime, String endTime) {
		//this.vin = vin;
		this.startTime = startTime;
		this.endTime = endTime;
		this.queryStop = "0";
		this.queryAlarm = "0";
		this.filterInvalid = "0";
		this.queryOverspeed = "0";
		this.plateNum = plateNum;
	}

}
