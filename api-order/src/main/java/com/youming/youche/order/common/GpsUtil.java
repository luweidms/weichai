package com.youming.youche.order.common;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.util.JsonHelper;
import com.youming.youche.util.SysMagUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @date 2022/3/17 16:20
 */
@Component
public class GpsUtil {

    private static final Log log = LogFactory.getLog(GpsUtil.class);
    private static final double EARTH_RADIUS = 6371000;
    private static final String baiduAK = "mFkUtX0BrhWfqVvLgIu5EaxkaqOENhw8";

    public static Map getaddressLocation(String cityName, String districtName, String adderDel) throws Exception {
        String adder = "";
        if (adderDel != null && !adderDel.equals("")) {
            adder = adderDel;
        }
        String cistrictBeanName = URLEncoder.encode(districtName + adder, "UTF-8");
        String url = "https://api.map.baidu.com/geocoder/v2/?address=" + cistrictBeanName + "&city=" + cityName + "&ak=" + baiduAK + "&output=json";
        HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url).openConnection());
        urlConnection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        String lines = reader.readLine();
        reader.close();
        urlConnection.disconnect();
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.fromObject(lines);
        } catch (Exception e) {
            log.error("第一次获取经纬度返回信息：" + lines + "；转换成json出错！", e);
            throw new Exception("获取地址经纬度异常！");
        }
        String status = jsonObject.getString("status");
        Map map = new HashMap();
        if (!status.equals("0")) {
            log.info("请求百度地址：" + url);
            if (status.equals("1")) {
                //throw new Exception("服务器内部错误 ！");
                //获取不到经纬度的情况
                cityName = URLEncoder.encode(cityName, "UTF-8");
                url = "https://api.map.baidu.com/geocoder/v2/?address=" + cityName + "&city=" + cityName + "&ak=" + baiduAK + "&output=json";
                urlConnection = (HttpURLConnection) (new URL(url).openConnection());
                urlConnection.connect();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                lines = reader.readLine();
                reader.close();
                urlConnection.disconnect();
                try {
                    jsonObject = JSONObject.fromObject(lines);
                } catch (Exception e) {
                    log.error("第二次获取经纬度返回信息：" + lines + "；转换成json出错！", e);
                    throw new Exception("获取地址经纬度异常！");
                }
            } else if (status.equals("2")) {
                throw new Exception("请求参数非法 ！");
            } else if (status.equals("3")) {
                throw new Exception("权限校验失败 ！");
            } else if (status.equals("4")) {
                throw new Exception("配额校验失败 ！");
            } else if (status.equals("5")) {
                throw new Exception("ak不存在或者非法 ！");
            } else if (status.equals("101")) {
                throw new Exception("服务禁用 ！");
            } else if (status.equals("102")) {
                throw new Exception("不通过白名单或者安全码不对 ！");
            } else if (status.equals("2xx")) {
                throw new Exception("无权限 ！");
            } else if (status.equals("3xx")) {
                throw new Exception("配额错误  ！");
            } else {
                throw new Exception("转换经纬度异常！");
            }
        }
        map.put("status", status);
        String result = jsonObject.getString("result");
        JSONObject jsonObj = JSONObject.fromObject(result);
        String location = jsonObj.getString("location");
        JSONObject jsonlocation = JSONObject.fromObject(location);
        String lng = jsonlocation.getString("lng");
        String lat = jsonlocation.getString("lat");
        if (adderDel != null && !adderDel.equals("")) {
            map.put("lng", lng);
            map.put("lat", lat);
        } else {
            getOffsetdata(lng, lat, map);
        }
        String precise = jsonObj.getString("precise");
        map.put("precise", precise);
        String confidence = jsonObj.getString("confidence");
        map.put("confidence", confidence);
        String level = jsonObj.getString("level");
        map.put("level", level);
        return map;
    }

    /**
     * 计算偏移位置
     *
     * @param lng
     * @param lat
     * @return
     * @throws Exception
     */
    public static Map getOffsetdata(String lng, String lat, Map map) throws Exception {
        String fLng = lng.substring(0, 6);
        String fLat = lat.substring(0, 6);
        if (lng.length() > 9) {
            String lLng = lng.substring(9, lng.length());
            lng = fLng + SysMagUtil.getRandomNumber(3) + lLng;
        }
        if (lat.length() > 9) {
            String lLat = lat.substring(9, lat.length());
            lat = fLat + SysMagUtil.getRandomNumber(3) + lLat;
        }
        map.put("lng", lng);
        map.put("lat", lat);
        return map;
    }

    /**
     * 驾驶线路规划(V2)
     * @param originEand
     * @param originNand
     * @param destinationEand
     * @param destinationNand
     * @param waypoints 途径点坐标串，支持20个以内的有序途径点。多个途径点坐标按顺序以英文竖线符号分隔，示例： 40.465,116.314|40.232,116.352|40.121,116.453
     * @param tactics       0：默认
    3：不走高速
    4：高速优先
    5：躲避拥堵
    6：少收费
    7：躲避拥堵&高速优先
    8：躲避拥堵&不走高速
    9：躲避拥堵&少收费
    10：躲避拥堵&不走高速&少收费
    11：不走高速&少收费
     * @return
     */
    public static Map getDirectionV2(Double originEand,Double originNand,Double destinationEand,Double destinationNand,String waypoints,
                                     int tactics){
        if(originNand==null || originNand.equals("") || originNand < 0){
            throw new BusinessException("请输入起点纬度!");
        }
        if(originEand==null || originEand.equals("") || originEand < 0){
            throw new BusinessException("请输入起点经度!");
        }
        if(destinationNand==null || destinationNand.equals("") || destinationNand < 0){
            throw new BusinessException("请输入终点纬度!");
        }
        if(destinationEand==null || destinationEand.equals("") || destinationEand < 0){
            throw new BusinessException("请输入终点经度!");
        }
        String url = "https://api.map.baidu.com/direction/v2/driving?"+"origin="+originNand+","+originEand
                +"&destination="+destinationNand+","+destinationEand
                +(StringUtils.isNotBlank(waypoints) ? "&waypoints="+waypoints : "" )
                + (tactics >= 0 && tactics <=10 ? "&tactics="+tactics : "" )
                +"&ak="+baiduAK;

        try{
            HttpURLConnection urlConnection = (HttpURLConnection)(new URL(url).openConnection());
            urlConnection.connect();
            String lines = IOUtils.toString(urlConnection.getInputStream(),"UTF-8");;
            urlConnection.disconnect();
            Map reqMap= JsonHelper.parseJSON2Map(lines);
            int stats = Integer.parseInt(reqMap.get("status")+"");
            if(stats!=0){
                throw new BusinessException("线路规划异常，错误码："+stats);
            }
//        Map<String,String> data = RemoteCacheUtil.hgetAll(EnumConsts.RemoteCache.OIL_LOCATION_INFO);
            Map resultMap = (Map)reqMap.get("result");
            List<Map> routesList = (List)resultMap.get("routes");
            log.info("路线规划地址:"+url);
            if(stats!=0){
                log.info("线路规划异常，错误码："+stats);
                throw new BusinessException("线路规划异常，错误码："+stats);
            }
            long distance=0;
            long duration=0;
            List<Map> list1 = new ArrayList<>();
            List<Long> oilIds = new ArrayList<>();
            List<Map> stepsList = null;
            if(routesList!=null && routesList.size()>0){
                for(Map routesMap :  routesList){
                    stepsList = (List)routesMap.get("steps");
                    distance +=Long.parseLong(routesMap.get("distance")+"");
                    duration +=Long.parseLong(routesMap.get("duration")+"");
                }
            }
            Map mapParam = new HashMap();
            mapParam.put("distance", distance);
            mapParam.put("stepsList", stepsList);
            mapParam.put("duration", duration);
            return mapParam;

        } catch (Exception e) {
            log.error("驾驶线路规划(V2)数据有误");
        }
        return null;
    }

    public static double hav(double theta) {
        double s = Math.sin(theta / 2);
        return s * s;
    }

    public static double getDistance(double lat0, double lng0, double lat1, double lng1) {
        lat0 = Math.toRadians(lat0);
        lat1 = Math.toRadians(lat1);
        lng0 = Math.toRadians(lng0);
        lng1 = Math.toRadians(lng1);
        double dlng = Math.abs(lng0 - lng1);
        double dlat = Math.abs(lat0 - lat1);
        double h = hav(dlat) + Math.cos(lat0) * Math.cos(lat1) * hav(dlng);
        double distance = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(h));
        return distance;
    }

}
