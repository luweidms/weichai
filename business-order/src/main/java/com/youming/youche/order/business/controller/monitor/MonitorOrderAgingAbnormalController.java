package com.youming.youche.order.business.controller.monitor;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.monitor.IMonitorOrderAgingAbnormalService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingAbnormal;
import com.youming.youche.order.dto.monitor.EventsInfoDto;
import com.youming.youche.order.dto.monitor.EventsStaticDataDto;
import com.youming.youche.order.dto.monitor.EventsVehicleDto;
import com.youming.youche.order.dto.monitor.EventsVehicleNewDto;
import com.youming.youche.order.dto.monitor.Location;
import com.youming.youche.order.dto.monitor.MonitorOrderAgingDto;
import com.youming.youche.order.dto.monitor.OrderLineDto;
import com.youming.youche.order.vo.monitor.MonitorOrderAgingVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营大屏
 *
 * @author hzx
 * @date 2022/3/9 10:40
 */
@RestController
@RequestMapping("monitor/order/aging")
public class MonitorOrderAgingAbnormalController extends BaseController<MonitorOrderAgingAbnormal, IMonitorOrderAgingAbnormalService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorOrderAgingAbnormalController.class);

    @DubboReference(version = "1.0.0")
    IMonitorOrderAgingAbnormalService iMonitorOrderAgingService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;

    @Override
    public IMonitorOrderAgingAbnormalService getService() {
        return iMonitorOrderAgingService;
    }

    /**
     * 晚靠台、异常停留、堵车缓行、预计延迟、迟到
     */
    @PostMapping("getAgingCount")
    public ResponseResult getAgingCount(MonitorOrderAgingVo monitorOrderAgingVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        MonitorOrderAgingDto monitorOrderAgingDto = iMonitorOrderAgingService.queryMonitorOrderAgingCount(monitorOrderAgingVo, accessToken);

        return ResponseResult.success(monitorOrderAgingDto);
    }

    /**
     * 盘点、无单24h+、无单48h+、无单72h+
     */
    @PostMapping("getCheckData")
    public ResponseResult getCheckData(MonitorOrderAgingVo monitorOrderAgingVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<EventsStaticDataDto> checkData = iMonitorOrderAgingService.getCheckData(monitorOrderAgingVo, accessToken);

        return ResponseResult.success(checkData);
    }

    /**
     * 行驶证审、交强险、商业险、其他险、营运证审、保养
     */
    @PostMapping("getExpireData")
    public ResponseResult getExpireData(MonitorOrderAgingVo monitorOrderAgingVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<EventsStaticDataDto> expireData = iMonitorOrderAgingService.getExpireData(monitorOrderAgingVo, accessToken);

        return ResponseResult.success(expireData);
    }

    /**
     * 时效事件下的车辆
     */
    @PostMapping("getVehicleListByAging")
    public ResponseResult getVehicleListByAging(MonitorOrderAgingVo monitorOrderAgingVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<EventsVehicleDto> eventsVehicleDtos = new ArrayList<>();
        eventsVehicleDtos = monitorOrderAgingVo.getVehicleClass() == null || monitorOrderAgingVo.getVehicleClass() <= 1 ?
                iMonitorOrderAgingService.queryEventsVehicleOutNew(monitorOrderAgingVo, accessToken) : new ArrayList<EventsVehicleDto>();

        return ResponseResult.success(eventsVehicleDtos);
    }

    @GetMapping("getVehicleListByAgingExport")
    public ResponseResult getVehicleListByAgingExport(MonitorOrderAgingVo monitorOrderAgingVo) {

        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("异常车辆列表导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iMonitorOrderAgingService.getVehicleListByAgingExport(monitorOrderAgingVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败异常" + e);
            return ResponseResult.failure("导出异常");
        }

    }

    /**
     * 盘点事件异常车辆
     */
    @GetMapping("getVehicleList")
    public ResponseResult getVehicleList(MonitorOrderAgingVo monitorOrderAgingVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<EventsVehicleNewDto> vehicleListMap = iMonitorOrderAgingService.getVehicleListMap(monitorOrderAgingVo, accessToken);

        return ResponseResult.success(vehicleListMap);
    }

    /**
     * 盘点事件异常车辆导出
     */
    @GetMapping("getVehicleListExport")
    public ResponseResult getVehicleListExport(MonitorOrderAgingVo monitorOrderAgingVo) {

        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("异常车辆列表导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iMonitorOrderAgingService.getVehicleListMapExport(monitorOrderAgingVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败异常" + e);
            return ResponseResult.failure("导出异常");
        }
    }

    /**
     * 异常车辆列表
     *
     * @param eventCode   车牌号
     * @param plateNumber 事件编号
     */
    @PostMapping("getDetail")
    public ResponseResult getDetail(@RequestParam("eventCode") String eventCode, @RequestParam("plateNumber") String plateNumber) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        EventsInfoDto eventsByVehicle = iMonitorOrderAgingService.getEventsByVehicle(eventCode, plateNumber, accessToken);

        return ResponseResult.success(eventsByVehicle);
    }

    /**
     * 获取线路规划路线
     *
     * @return
     * @throws Exception
     */
    @GetMapping("queryOrderLineList")
    public ResponseResult queryOrderLineList(@RequestParam("orderId") Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderLineDto> list = orderSchedulerService.queryOrderLineList(orderId, accessToken);
        return ResponseResult.success(list);
    }

    /**
     * 获取订单车辆实际路线以及异常点
     *
     * @return
     * @throws Exception
     */
    @GetMapping("queryOrderTravelTrack")
    public ResponseResult queryOrderTravelTrack(@RequestParam("orderId") Long orderId, @RequestParam("agingType") Integer agingType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map<String, Object> rtnMap = new HashMap<String, Object>(2);
        // 查询实际行驶轨迹
        List<Location> list = iMonitorOrderAgingService.queryOrderTravelTrack(orderId, accessToken);
        rtnMap.put("drivingLine", list);
        //查询订单事件集合
        List<Object> list2 = iMonitorOrderAgingService.queryMonitorOrderAgingList(orderId, agingType);
        rtnMap.put("orderAgings", list2);
        return ResponseResult.success(rtnMap);
    }


    /**
     * 轨迹回放
     * http://cd.tongxin.cn/order/tracking/trackingBO.ajax?cmd=queryOrderLineList
     * http://cd.tongxin.cn/order/tracking/trackingBO.ajax?cmd=queryOrderTravelTrack
     */

}
