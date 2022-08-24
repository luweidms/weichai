package com.youming.youche.cloud.business.controller.tsp;

import com.youming.youche.cloud.api.tsp.ITspVehicleTrackService;
import com.youming.youche.cloud.vo.tsp.TspVehicleTrackVo;
import com.youming.youche.commons.response.ResponseResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车联网车辆轨迹前端控制器
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@RestController
@RequestMapping("/tsp/vehicle/track")
public class TspVehicleTrackControllerApi {
	@DubboReference(version = "1.0.0")
	ITspVehicleTrackService tspVehicleTrackService;

	/***
	 * 查询车辆归结列表
	 * @author shilei
	 * @date 2022/3/21 11:25
	 * @param vin 车架号
	 * @return TspVehicleTrackDto
	 **/
	@GetMapping({"getVehicleTrackList"})
	public ResponseResult getVehicleTrackList(@RequestParam("vin") String vin,
											  @RequestParam("orderId") Long orderId) {
		return ResponseResult.success(tspVehicleTrackService.getVehicleTrackList(vin, orderId));
	}

	/***
	 * 根据车架号列表查询最后位置列表
	 * @author shilei
	 * @date 2022/3/21 11:25
	 * @param vins 车架号
	 * @return TspVehicleTrackDto
	 **/
	@GetMapping({"getVehicleTrackListByVinList"})
	public ResponseResult getVehicleTrackList(@RequestParam("vins") String vins) {
		return ResponseResult.success(tspVehicleTrackService.getVehicleTrackListByVinList(vins));
	}
}
