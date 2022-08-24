package com.youming.youche.cloud.api.tsp;

import com.youming.youche.cloud.domin.sms.MsgNotifySetting;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.cloud.dto.tsp.TspVehicleWarningDto;
import com.youming.youche.cloud.vo.tsp.TspVehicleWarningVo;
import com.youming.youche.commons.base.IBaseService;

import java.util.List;

/**
 * 车联网车辆告警接口
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
public interface ITspVehicleWarningService{

	/***
	 * 查询车辆告警列表
	 * @author shilei
	 * @date 2022/3/21 11:25
	 * @param tenantId 车队ID
	 * @param vehicleClass 车辆种类
	 * @param plateNumber 车牌号
	 * @param sourceRegion 始发市
	 * @param desRegion 到达市
	 * @return TspVehicleTrackDto
	 **/
	List<TspVehicleWarningVo> getVehicleWarningList(String tenantId, String vehicleClass, String plateNumber, String sourceRegion, String desRegion);
}
