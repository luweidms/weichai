package com.youming.youche.cloud.provider.service.tsp;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youming.youche.cloud.api.tsp.ITspVehicleWarningService;
import com.youming.youche.cloud.constant.TspConst;
import com.youming.youche.cloud.dto.tsp.TspVehicleInfoDto;
import com.youming.youche.cloud.dto.tsp.TspVehicleWarningDto;
import com.youming.youche.cloud.provider.service.sms.SysSmsSendServiceImpl;
import com.youming.youche.cloud.vo.tsp.TspVehicleWarningVo;
import com.youming.youche.commons.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 车联网车辆轨迹服务实现
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 * @version 1.0
 */
@DubboService(version = "1.0.0")
public class TspVehicleWarningServiceImpl implements ITspVehicleWarningService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SysSmsSendServiceImpl.class);

	@Value("${tsp.warning.url}")
	String url;

	@Value("${tsp.warning.duration-limit}")
	String warningDurationLimit;


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
	@Override
	public List<TspVehicleWarningVo> getVehicleWarningList(String tenantId, String vehicleClass, String plateNumber, String sourceRegion, String desRegion){
		LOGGER.info("---------------start query vehicle warning! -----------");

		try {
			TspVehicleInfoDto tspVehicleWarningVo = getVehicleInfoForTsp(plateNumber);

			//默认返回一天的数据
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String sBeginTime = LocalDateTime.now().minusDays(1).format(dateTimeFormatter);
			String sEndTime = LocalDateTime.now().format(dateTimeFormatter);;

			List<TspVehicleWarningVo> resultList = new ArrayList<>();
			TspVehicleWarningDto tspVehicleWarningDto = new TspVehicleWarningDto(tspVehicleWarningVo.getVin(), sBeginTime, sEndTime);
			String results = HttpUtil.createPost(url).
					header("Content-Type", "application/json;charset=UTF-8").
					body(JSONUtil.toJsonStr(tspVehicleWarningDto)).execute().charset("utf-8").body();
			if (results != null) {
				JSONObject jsonResult = JSONUtil.parseObj(results);
				if (null != jsonResult.get(TspConst.CODE) && TspConst.SUCCESS.equals(jsonResult.get(TspConst.CODE).toString())) {
					JSONObject jsonShellData = JSONUtil.parseObj(jsonResult.get(TspConst.DATA));
					JSONArray jsonArray = jsonShellData.getJSONArray(TspConst.DATA);
					resultList = JSONUtil.toList(jsonArray, TspVehicleWarningVo.class);
					for(TspVehicleWarningVo tempRecord: resultList){
						tempRecord.setPlateNumber(plateNumber);
						tempRecord.setVin(tspVehicleWarningDto.getVin());
						tempRecord.setCompanyName(tspVehicleWarningVo.getCompanyName());
						tempRecord.setOrderId(tspVehicleWarningVo.getOrderId());
						tempRecord.setDriverName(tspVehicleWarningVo.getDriverName());
					}
				} else {
					throw new BusinessException("请求车联网接口发送异常:" + TspConst.MSG);
				}
			}
			return resultList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("请求车联网接口发送异常:" + e);
		}
	}

	/***
	 * 跨服务调用，返回车辆当前进行的客户名称 客户路线名称 车辆类别名称 订单号码 主驾名 VIN
	 * @author shilei
	 * @date 2022/3/21 11:25
	 * @param plateNumber 车牌号
	 * @return TspVehicleInfoDto 需填写字段：companyName vin customerLineName vehicleClassName orderId driverName
	 **/
	private TspVehicleInfoDto getVehicleInfoForTsp(String plateNumber){
		TspVehicleInfoDto tspVehicleWarningVo = new TspVehicleInfoDto();
		//* todo
		//tspVehicleWarningVo = recordService.getVehicleInfoForTsp;
		tspVehicleWarningVo.setVin("LZZ7CLVC5KC268327");
		return tspVehicleWarningVo;
	}
}
