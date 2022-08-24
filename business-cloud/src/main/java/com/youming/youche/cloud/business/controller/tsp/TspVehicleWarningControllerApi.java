package com.youming.youche.cloud.business.controller.tsp;

import com.youming.youche.cloud.api.tsp.ITspVehicleWarningService;
import com.youming.youche.commons.response.ResponseResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * 车联网车辆告警前端控制器
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@RestController
@RequestMapping("/tsp/vehicle-warning")
public class TspVehicleWarningControllerApi {
	@DubboReference(version = "1.0.0")
	ITspVehicleWarningService tspVehicleWarningService;

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
	@GetMapping({"getVehicleWarningList"})
	public ResponseResult getVehicleWarningList(@RequestParam(value = "tenantId", required = false) String tenantId,
	                                          @RequestParam(value = "vehicleClass", required = false) String vehicleClass,
	                                          @RequestParam(value = "plateNumber", required = false) String plateNumber,
	                                          @RequestParam(value = "sourceRegion", required = false) String sourceRegion,
	                                          @RequestParam(value = "desRegion", required = false) String desRegion) {
		return ResponseResult.success(tspVehicleWarningService.getVehicleWarningList(tenantId, vehicleClass, plateNumber, sourceRegion, desRegion));
	}
}
