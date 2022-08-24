package com.youming.youche.cloud.provider.service.tsp;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youming.youche.cloud.api.tsp.ITspVehicleTrackService;
import com.youming.youche.cloud.constant.TspConst;
import com.youming.youche.cloud.dto.tsp.TspVehicleTrackDto;
import com.youming.youche.cloud.provider.service.sms.SysSmsSendServiceImpl;
import com.youming.youche.cloud.vo.tsp.TspVehicleTrackVo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.util.Geocoder;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车联网车辆轨迹服务实现
 *
 * @author shilei
 * @version 1.0
 * @date 22/3/21 11:21
 */
@DubboService(version = "1.0.0")
public class TspVehicleTrackServiceImpl implements ITspVehicleTrackService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TspVehicleTrackServiceImpl.class);

    @Value("${tsp.track.url}")
    String url;

    @Value("${tsp.track.duration-limit}")
    String trackDurationLimit;

//    @DubboReference(version = "1.0.0")
//    IOrderSchedulerService orderSchedulerService;
//
//    @DubboReference(version = "1.0.0")
//    IOrderSchedulerHService orderSchedulerServiceH;

    /***
     * 根据车架号列表查询最后位置列表
     * @author shilei
     * @date 2022/3/21 11:25
     * @param plateNum 车牌号
     * @return TspVehicleTrackDto
     **/
    @Override
    public List<TspVehicleTrackVo> getVehicleTrackListByVinList(String plateNum) {
        List<TspVehicleTrackVo> resultList = new ArrayList<>();
        try {
            LocalDateTime tEndTime = LocalDateTime.now();
            LocalDateTime tBeginTime = tEndTime.minusDays(1);
            String[] vinStrList = plateNum.split(TspConst.SPLIT_CHAR);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (String vin : vinStrList) {
                TspVehicleTrackDto tspVehicleTrackDto = new TspVehicleTrackDto(plateNum, tBeginTime.format(dateTimeFormatter), tEndTime.format(dateTimeFormatter));
                String results = HttpUtil.createPost(url).
                        header("Content-Type", "application/json;charset=UTF-8").
                        body(JSONUtil.toJsonStr(tspVehicleTrackDto)).execute().charset("utf-8").body();
                if (results != null) {
                    JSONObject jsonResult = JSONUtil.parseObj(results);
                    LOGGER.info(jsonResult.toString());
                    if (null != jsonResult.get(TspConst.CODE) && TspConst.SUCCESS.equals(jsonResult.get(TspConst.CODE).toString())) {
                        JSONObject jsonShellData = JSONUtil.parseObj(jsonResult.get(TspConst.DATA));
                        JSONArray jsonArray = jsonShellData.getJSONArray(TspConst.DATA);
                        List<TspVehicleTrackVo> tmpResultList = JSONUtil.toList(jsonArray, TspVehicleTrackVo.class);
                        if (tmpResultList.size() > 0) {
                            TspVehicleTrackVo tempRecord = tmpResultList.get(0);
                            tempRecord.setVin(tspVehicleTrackDto.getVin());
                            if(tempRecord.getLng() != null && tempRecord.getLat() != null) {
                                double[] map = Geocoder.wgs84_To_Bd09(Double.parseDouble(tempRecord.getLng()), Double.parseDouble(tempRecord.getLat()));
                                if (map != null && map.length == 2 && map[0] != 0) {
                                    tempRecord.setLng(String.valueOf(map[0]));
                                }
                                if (map != null && map.length == 2 && map[1] != 0) {
                                    tempRecord.setLat(String.valueOf(map[1]));
                                }
                            }
                            resultList.add(tempRecord);
                        }
                    } else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        //    throw new BusinessException("请求车联网接口发送异常");
        }
        return resultList;
    }

    /***
     * 查询车辆归结列表
     * @author shilei
     * @date 2022/3/21 11:25
     * @param plateNum 车牌号
     * @return TspVehicleTrackDto
     **/
    public List<TspVehicleTrackVo> testGps(String plateNum,String startDate,String endDate) {
        LOGGER.info("---------------start query vehicle track! -----------");
        List<TspVehicleTrackVo> resultList = new ArrayList<>();
        try {
            TspVehicleTrackDto tspVehicleTrackDto = new TspVehicleTrackDto(plateNum, startDate, endDate);
            LOGGER.info(JSONUtil.toJsonStr(tspVehicleTrackDto));
            String results = HttpUtil.createPost("https://wit-jt809-api-prod.tongxin.cn/jt809/vehicle/getVehicleTrace").
                    header("Content-Type", "application/json;charset=UTF-8").
                    body(JSONUtil.toJsonStr(tspVehicleTrackDto)).execute().charset("utf-8").body();
            if (results != null) {
                JSONObject jsonResult = JSONUtil.parseObj(results);
                if (null != jsonResult.get(TspConst.CODE) && TspConst.SUCCESS.equals(jsonResult.get(TspConst.CODE).toString())) {
                    JSONObject jsonShellData = JSONUtil.parseObj(jsonResult.get(TspConst.DATA));
                    JSONArray jsonArray = jsonShellData.getJSONArray(TspConst.DATA);
                    resultList = JSONUtil.toList(jsonArray, TspVehicleTrackVo.class);
                    for (TspVehicleTrackVo tempRecord : resultList) {
                        tempRecord.setVin(tspVehicleTrackDto.getVin());
                        if(tempRecord.getLng() != null && tempRecord.getLat() != null) {
                            double[] map = Geocoder.wgs84_To_Bd09(Double.parseDouble(tempRecord.getLng()), Double.parseDouble(tempRecord.getLat()));
                            if (map != null && map.length == 2 && map[0] != 0) {
                                tempRecord.setLng(String.valueOf(map[0]));
                            }
                            if (map != null && map.length == 2 && map[1] != 0) {
                                tempRecord.setLat(String.valueOf(map[1]));
                            }
                        }
                        //tempRecord.setCompanyName(tspVehicleTrackVo.getCompanyName());
                        //tempRecord.setOrderId(tspVehicleTrackVo.getOrderId());
                        //tempRecord.setDriverName(tspVehicleTrackVo.getDriverName());
                    }
                } else {
                   // throw new BusinessException("请求车联网接口发送异常:" + TspConst.MSG);
                }
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
          //  throw new BusinessException("请求车联网接口发送异常:" + e);
        }
        return resultList;
    }



    /***
     * 查询车辆归结列表
     * @author shilei
     * @date 2022/3/21 11:25
     * @param plateNum 车牌号
     * @return TspVehicleTrackDto
     **/
    @Override
    public List<TspVehicleTrackVo> getVehicleTrackList(String plateNum, Long orderId) {
        LOGGER.info("---------------start query vehicle track! -----------");
        String beginTime = "";
        String endTime = "";
        LocalDateTime tEndTime = LocalDateTime.now();
        LocalDateTime tBeginTime = tEndTime.minusDays(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        beginTime = tBeginTime.format(dateTimeFormatter);
        endTime = tEndTime.format(dateTimeFormatter);
        List<TspVehicleTrackVo> resultList = new ArrayList<>();
        try {
//            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
//            if(orderScheduler == null || orderScheduler.getOrderId() == null){
//                OrderSchedulerH orderSchedulerH = orderSchedulerServiceH.getOrderSchedulerH(orderId);
//                if(orderSchedulerH.getCarDependDate() == null){
//                    if(orderSchedulerH.getCarDependDate() == null) {
//                        throw new BusinessException("订单没有靠台时间，无法展示轨迹信息");
//                    }else{
//                        beginTime =   DateUtil.formatLocalDateTime(orderSchedulerH.getDependTime(),DateUtil.DATETIME_FORMAT);
//                    }
//                }else {
//                    beginTime = DateUtil.formatLocalDateTime(orderSchedulerH.getCarDependDate(), DateUtil.DATETIME_FORMAT);
//                }
//                if(orderSchedulerH.getCarArriveDate() != null){
//                    endTime = DateUtil.formatLocalDateTime(orderSchedulerH.getCarArriveDate(),DateUtil.DATETIME_FORMAT);
//                }else{
//                    LocalDateTime tBeginTime = orderSchedulerH.getCarDependDate().minusDays(1);
//                    endTime = DateUtil.formatLocalDateTime(tBeginTime,DateUtil.DATETIME_FORMAT);
//                }
//            }else{
//                if(orderScheduler.getCarDependDate() == null){
//                    if(orderScheduler.getDependTime() == null) {
//                        throw new BusinessException("订单没有靠台时间，无法展示轨迹信息");
//                    }else{
//                        beginTime =   DateUtil.formatLocalDateTime(orderScheduler.getDependTime(),DateUtil.DATETIME_FORMAT);
//                    }
//                }else {
//                    beginTime = DateUtil.formatLocalDateTime(orderScheduler.getCarDependDate(), DateUtil.DATETIME_FORMAT);
//                }
//                if(orderScheduler.getCarArriveDate() != null){
//                    endTime = DateUtil.formatLocalDateTime(orderScheduler.getCarArriveDate(),DateUtil.DATETIME_FORMAT);
//                }else{
//                    LocalDateTime tBeginTime = orderScheduler.getCarDependDate().minusDays(1);
//                    endTime = DateUtil.formatLocalDateTime(tBeginTime,DateUtil.DATETIME_FORMAT);
//                }
//            }
            //前端请求数据，没有秒字段，请求车联网接口，日期格式需要秒
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //	String sBeginTime = beginTime + ":00";
            //	String sEndTime = endTime + ":00";
//            LocalDateTime tBeginTime = LocalDateTime.parse(beginTime, dateTimeFormatter);
//            LocalDateTime tEndTime = LocalDateTime.parse(endTime, dateTimeFormatter);

            //TspVehicleInfoDto tspVehicleTrackVo = getVehicleInfoForTsp(vin);

            //		判断最大查询时间区间不能超过限制
//            long lTrackDurationLimit = Long.parseLong(trackDurationLimit);
//            if (tBeginTime.plusHours(lTrackDurationLimit).compareTo(tEndTime) < 0) {
//                endTime = DateUtil.formatLocalDateTime(tBeginTime,DateUtil.DATETIME_FORMAT);
//            }
            //发送短信
            TspVehicleTrackDto tspVehicleTrackDto = new TspVehicleTrackDto(plateNum, beginTime, endTime);
            LOGGER.info(JSONUtil.toJsonStr(tspVehicleTrackDto));
            String results = HttpUtil.createPost(url).
                    header("Content-Type", "application/json;charset=UTF-8").
                    body(JSONUtil.toJsonStr(tspVehicleTrackDto)).execute().charset("utf-8").body();
            if (results != null) {
                JSONObject jsonResult = JSONUtil.parseObj(results);
                if (null != jsonResult.get(TspConst.CODE) && TspConst.SUCCESS.equals(jsonResult.get(TspConst.CODE).toString())) {
                    JSONObject jsonShellData = JSONUtil.parseObj(jsonResult.get(TspConst.DATA));
                    JSONArray jsonArray = jsonShellData.getJSONArray(TspConst.DATA);
                    resultList = JSONUtil.toList(jsonArray, TspVehicleTrackVo.class);
                    for (TspVehicleTrackVo tempRecord : resultList) {
                        //tempRecord.setPlateNumber(plateNumber);
                        tempRecord.setVin(tspVehicleTrackDto.getVin());
                        if(tempRecord.getLng() != null && tempRecord.getLat() != null) {
                            double[] map = Geocoder.wgs84_To_Bd09(Double.parseDouble(tempRecord.getLng()), Double.parseDouble(tempRecord.getLat()));
                            if (map != null && map.length == 2 && map[0] != 0) {
                                tempRecord.setLng(String.valueOf(map[0]));
                            }
                            if (map != null && map.length == 2 && map[1] != 0) {
                                tempRecord.setLat(String.valueOf(map[1]));
                            }
                        }
                        //tempRecord.setCompanyName(tspVehicleTrackVo.getCompanyName());
                        //tempRecord.setOrderId(tspVehicleTrackVo.getOrderId());
                        //tempRecord.setDriverName(tspVehicleTrackVo.getDriverName());
                    }
                } else {
                    // throw new BusinessException("请求车联网接口发送异常:" + TspConst.MSG);
                }
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            //  throw new BusinessException("请求车联网接口发送异常:" + e);
        }
        return resultList;
    }

    public static void main(String[] args) {
        TspVehicleTrackServiceImpl tspVehicleTrackService = new TspVehicleTrackServiceImpl();
        List<TspVehicleTrackVo> tspVehicleTrackVos =  tspVehicleTrackService.testGps("皖A12345","2020-08-05 00:00:00","2020-08-05 23:00:00");
        for (TspVehicleTrackVo tspVehicleTrackVo:tspVehicleTrackVos
             ) {
            System.out.println(tspVehicleTrackVo.toString());
        }
    }


}
