package com.youming.youche.order.api.monitor;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingAbnormal;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.monitor.EventsInfoDto;
import com.youming.youche.order.dto.monitor.EventsStaticDataDto;
import com.youming.youche.order.dto.monitor.EventsVehicleDto;
import com.youming.youche.order.dto.monitor.EventsVehicleNewDto;
import com.youming.youche.order.dto.monitor.Location;
import com.youming.youche.order.dto.monitor.MonitorOrderAgingDto;
import com.youming.youche.order.vo.monitor.MonitorOrderAgingVo;

import java.util.List;
import java.util.Map;

/**
 * 运营大屏
 *
 * @author hzx
 * @date 2022/3/9 10:42
 */
public interface IMonitorOrderAgingAbnormalService extends IBaseService<MonitorOrderAgingAbnormal> {
    /**
     * 查询异常数据
     * @param orderId
     * @param plateNumber
     * @param type
     * @param lineType
     * @param tenantId
     * @return
     */
    List<MonitorOrderAgingAbnormal> getMonitorOrderAgingAbnormal(Long orderId, String plateNumber, Integer type, Integer lineType, Long tenantId);

    /**
     * 查询时效数量
     *
     * @param monitorOrderAgingVo busiId 业务主键 tenantId 或 orderId
     *                            isTenant 是否车队
     */
    MonitorOrderAgingDto queryMonitorOrderAgingCount(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken);

    /**
     * 盘点数据统计
     */
    List<EventsStaticDataDto> getCheckData(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken);

    /**
     * 盘点数据统计
     */
    List<EventsStaticDataDto> getExpireData(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken);

    /**
     * 时效事件下的车辆
     * @param monitorOrderAgingVo
     * @param accessToken
     * @return
     */
    List<EventsVehicleDto> queryEventsVehicleOutNew(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken);

    /**
     * 时效事件下的车辆-导出
     * @param monitorOrderAgingVo
     * @param accessToken
     * @param importOrExportRecords
     */
    void getVehicleListByAgingExport(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 盘点事件异常车辆
     * @param monitorOrderAgingVo
     * @param accessToken
     * @return
     */
    List<EventsVehicleNewDto> getVehicleListMap(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken);

    /**
     * 盘点事件异常车辆-导出
     * @param monitorOrderAgingVo
     * @param accessToken
     * @param importOrExportRecords
     */
    void getVehicleListMapExport(MonitorOrderAgingVo monitorOrderAgingVo, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 获取车辆事件详细信息
     * @param plateNumber 车牌号
     * @return
     */
    EventsInfoDto getEventsByVehicle(String eventCode, String plateNumber, String accessToken);

    /**
     * 查询订单行驶轨迹
     * @param orderId
     * @return
     * @throws Exception
     */
    List<Location> queryOrderTravelTrack(Long orderId,String accessToken);

    /**
     * 设置实际经纬度
     */
    Map setRealLocation(int gpsType, OrderScheduler orderScheduler, Long orderId);

    /**
     * 查询订单时效集合
     *
     * @param orderId
     * @param agingType 类型区分
     */
    List<Object> queryMonitorOrderAgingList(Long orderId, Integer agingType);

    /**
     * 异常大屏-订单时效监控
     */
    String doTask(Map<String, Object> arg0);

    /**
     * 移如历史表
     *
     * @param orderId 订单id
     */
    void createAgingHis(Long orderId);

    /**
     * @param orderId  订单id
     * @param type     '类型:1 堵车缓行 2 异常停留'
     * @param lineType '类型:0 为起始地 >0 为经停点(经停点序号)'
     * @return
     */
    boolean deleteMonitorOrderAgingAbnormal(Long orderId, Integer type, Integer lineType);

}
