package com.youming.youche.cloud.api.tsp;

import com.youming.youche.cloud.dto.tsp.TspVehicleTrackDto;
import com.youming.youche.cloud.vo.tsp.TspVehicleTrackVo;
import com.youming.youche.commons.base.IBaseService;

import java.util.List;

/**
 * 车联网车辆轨迹接口
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
public interface ITspVehicleTrackService {

	/***
	 * 查询车辆轨迹列表
	 * @author shilei
	 * @date 2022/3/21 11:25
	 * @param vin 车架号
	 * @return TspVehicleTrackDto
	 **/
	List<TspVehicleTrackVo> getVehicleTrackList(String vin, Long orderId);

	/***
	 * 根据车架号列表查询最后位置列表
	 * @author shilei
	 * @date 2022/3/21 11:25
	 * @param vins 车架号
	 * @return TspVehicleTrackDto
	 **/
	List<TspVehicleTrackVo> getVehicleTrackListByVinList(String vins);
}

