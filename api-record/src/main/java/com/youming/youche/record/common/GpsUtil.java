package com.youming.youche.record.common;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.util.JsonHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 向子俊
 * @version 1.0.0
 * @ClassName GpsUtil.java
 * @Description TODO
 * @createTime 2022年02月23日 16:57:00
 */
@Component
public class GpsUtil {
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ReadisDatainfoUtil readisDatainfoUtil;

    private static final Log log = LogFactory.getLog(GpsUtil.class);
    private static final double EARTH_RADIUS = 6371000;
    //潍柴
   // private static final String baiduAK = "xNwsarM3juMLh86MtPAkpB7oWi2h9lE3";
    //个人
    private static final String baiduAK = "mFkUtX0BrhWfqVvLgIu5EaxkaqOENhw8";


    /**
     * 根据两点经纬度获取沿途油站
     * @param originEand
     * @param originNand
     * @param destinationEand
     * @param destinationNand
     * @return
     */
    public  List<Map> gainLineOilDepot(Double originEand,Double originNand,Double destinationEand,Double destinationNand,Long tenantId) throws Exception{
        if(originNand==null || originNand.equals("") || originNand < 0){
            throw new BusinessException("请输入起点纬度!");
        }
        if(originNand > 90){
            throw new BusinessException("输入的地点纬度有误！");
        }
        if(originEand==null || originEand.equals("") || originEand < 0){
            throw new BusinessException("请输入起点经度!");
        }
        if(originEand > 180){
            throw new BusinessException("输入的起点经度有误!");
        }
        if(destinationNand==null || destinationNand.equals("") || destinationNand < 0){
            throw new BusinessException("请输入终点纬度!");
        }
        if(destinationNand > 90){
            throw new BusinessException("输入的终点纬度有误！");
        }
        if(destinationEand==null || destinationEand.equals("") || destinationEand < 0){
            throw new BusinessException("请输入终点经度!");
        }
        if(destinationEand > 180){
            throw new BusinessException("输入的终点经度有误!");
        }
        if(tenantId==null  || tenantId < 0){
            throw new BusinessException("请输入租户!");
        }
        String url = "https://api.map.baidu.com/direction/v2/driving?origin="+originNand+","+originEand+"&destination="+destinationNand+","+destinationEand+"&ak="+baiduAK;
        HttpURLConnection urlConnection = (HttpURLConnection)(new URL(url).openConnection());
        urlConnection.connect();
        String lines = IOUtils.toString(urlConnection.getInputStream(),"UTF-8");;
        urlConnection.disconnect();
        Map reqMap= JsonHelper.parseJSON2Map(lines);
        int stats = Integer.parseInt(reqMap.get("status")+"");
        if(stats!=0){
            throw new BusinessException("线路规划异常，错误码："+stats);
        }
        Integer gl = Integer.parseInt(String.valueOf((readisDatainfoUtil.getSysCfg("OIL_DEPOT_CONFIG", "0").getCfgValue() == null ? 20 : readisDatainfoUtil.getSysCfg("OIL_DEPOT_CONFIG", "0").getCfgValue())));
        Map<String,String> data = (Map<String, String>) redisUtil.get(EnumConsts.RemoteCache.OIL_LOCATION_INFO);
        Map resultMap = (Map)reqMap.get("result");
        List<Map> routesList = (List)resultMap.get("routes");
        List<Map> list1 = new ArrayList<>();
        List<Long> oilIds = new ArrayList<>();
        Map<Long,Double> minDistance = new HashMap<>();
        if(routesList!=null && routesList.size()>0){
            for(Map routesMap :  routesList){
                List<Map> stepsList = (List)routesMap.get("steps");
                for (Map map : stepsList) {
                    Map endlocation = (Map)map.get("end_location");//获取分段终点经纬度
                    Double lat = Double.parseDouble(String.valueOf(endlocation.get("lat")));//分段点纬度
                    Double lng = Double.parseDouble(String.valueOf(endlocation.get("lng")));//分段点经度
                    Map<String, double[]> squareMap = returnLLSquarePoint(lng,lat, gl * 1000);
                    double[] leftTopPoint = squareMap.get("leftTopPoint");
                    double[] rightTopPoint = squareMap.get("rightTopPoint");
                    double[] leftBottomPoint = squareMap.get("leftBottomPoint");
                    double[] rightBottomPoint = squareMap.get("rightBottomPoint");

                    //计算点的范围位置
                    double maxNand =  leftTopPoint[0];
                    double minNand =  rightBottomPoint[0];
                    double minEand =  leftBottomPoint[1];
                    double maxEand =  rightTopPoint[1];

                    for(Map.Entry<String, String> entry : data.entrySet()){
                        String value = entry.getValue();
                        Map<String, Object> mm = JsonHelper.parseJSON2Map(value);
                        if (mm.get("oilId") != null && StringUtils.isNotBlank(String.valueOf(mm.get("oilId")))) {
                            Long oilId = Long.parseLong(String.valueOf(mm.get("oilId")));
                            if (oilId != null && oilId > 0) {
                                if (mm.get("businessType") != null && String.valueOf(SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL).equals(mm.get("businessType"))) {
                                    double eandDouble = Double.valueOf(String.valueOf(mm.get("eand")));
                                    double nandDouble = Double.valueOf(String.valueOf(mm.get("nand")));
                                    //处理数据
                                    if ((eandDouble <= maxEand && eandDouble >= minEand) && (nandDouble <= maxNand && nandDouble >= minNand)) {//四点范围内
                                        Double distanceMax = GpsUtil.getDistance(lat, lng, nandDouble, eandDouble);
                                        Double aDouble = minDistance.get(oilId);
                                        if (aDouble != null && aDouble < distanceMax) {
                                            distanceMax = aDouble;
                                        }
                                        minDistance.put(oilId, distanceMax);
                                        if (!oilIds.contains(oilId)) {
                                            mm.put("oilDistance", GpsUtil.getDistance(originNand, originEand, nandDouble, eandDouble));//油站与起始点距离
                                            oilIds.add(oilId);
                                            list1.add(mm);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(list1.size() > 0){
            for (Map map : list1) {
                long oilId = DataFormat.getLongKey(map,"oilId");
                Double aDouble = minDistance.get(oilId);
                if(aDouble != null){
                    map.put("distanceAlong",aDouble);
                }
            }
        }
        return list1;
    }

    /**
     *
     * @param longitude    经度
     * @param latitude    纬度
     * @param distance    范围（米）
     * @return
     */
    public static Map<String, double[]> returnLLSquarePoint(double longitude,  double latitude, double distance) {
        Map<String, double[]> squareMap = new HashMap<String, double[]>();
        // 计算经度弧度,从弧度转换为角度
        double dLongitude = 2 * (Math.asin(Math.sin(distance / (2 * EARTH_RADIUS)) / Math.cos(Math.toRadians(latitude))));
        dLongitude = Math.toDegrees(dLongitude);
        // 计算纬度角度
        double dLatitude = distance / EARTH_RADIUS;
        dLatitude = Math.toDegrees(dLatitude);
        // 正方形
        double[] leftTopPoint = { latitude + dLatitude, longitude - dLongitude };
        double[] rightTopPoint = { latitude + dLatitude, longitude + dLongitude };
        double[] leftBottomPoint = { latitude - dLatitude, longitude - dLongitude };
        double[] rightBottomPoint = { latitude - dLatitude, longitude + dLongitude };
        squareMap.put("leftTopPoint", leftTopPoint);
        squareMap.put("rightTopPoint", rightTopPoint);
        squareMap.put("leftBottomPoint", leftBottomPoint);
        squareMap.put("rightBottomPoint", rightBottomPoint);
        return squareMap;
    }
    public static Map getBaiduAdder(String coords) throws MalformedURLException, Exception {
        String url = "https://api.map.baidu.com/geocoder/v2/?location=" + coords + "&output=json&pois=0&ak=" + baiduAK;
        HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url).openConnection());
        urlConnection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        String lines = reader.readLine();
        reader.close();
        urlConnection.disconnect();
        JSONObject jsonObject = JSONObject.fromObject(lines);
        String status = jsonObject.getString("status");
        Map map = new HashMap();
        if (!status.equals("0")) {
            if (status.equals("1")) {
                log.error("服务器内部错误 ！");
            } else if (status.equals("2")) {
                log.error("请求参数非法 ！");
            } else if (status.equals("3")) {
                log.error("权限校验失败 ！");
            } else if (status.equals("4")) {
                log.error("配额校验失败 ！");
            } else if (status.equals("5")) {
                log.error("ak不存在或者非法 ！");
            } else if (status.equals("101")) {
                log.error("服务禁用 ！");
            } else if (status.equals("102")) {
                log.error("不通过白名单或者安全码不对 ！");
            } else if (status.equals("2xx")) {
                log.error("无权限 ！");
            } else if (status.equals("3xx")) {
                log.error("配额错误  ！");
            } else {
                log.error("转换异常！");
            }
        } else {
            map.put("status", status);
            String result = jsonObject.getString("result");
            JSONObject jsonObj = JSONObject.fromObject(result);
            String location = jsonObj.getString("location");
            JSONObject jsonlocation = JSONObject.fromObject(location);
            String lng = jsonlocation.getString("lng");
            map.put("lng", lng);
            String lat = jsonlocation.getString("lat");
            map.put("lat", lat);
            String formattedAddress = jsonObj.getString("formatted_address");
            map.put("formattedAddress", formattedAddress);
            String addressComponent = jsonObj.getString("addressComponent");
            JSONObject addressComponentJson = JSONObject.fromObject(addressComponent);
            String city = addressComponentJson.getString("city");
            map.put("city", city);
            String country = addressComponentJson.getString("country");
            map.put("country", country);
            String direction = addressComponentJson.getString("direction");
            map.put("direction", direction);
            String distance = addressComponentJson.getString("distance");
            map.put("distance", distance);
            String district = addressComponentJson.getString("district");
            map.put("district", district);
            String province = addressComponentJson.getString("province");
            map.put("province", province);
            String street = addressComponentJson.getString("street");
            map.put("street", street);
            String street_number = addressComponentJson.getString("street_number");
            map.put("street_number", street_number);
            String country_code = addressComponentJson.getString("country_code");
            map.put("country_code", country_code);
        }
        return map;
    }

    /**
     * 关键地址搜索
     *
     * @param region
     * @param query
     * @return
     * @throws Exception
     */
    public static List<Monitor> getSuggestion(String region, String query) throws BusinessException{
        List<Monitor> list = new ArrayList<Monitor>();
        try {
            if (region == null || region.equals("")) {
                throw new BusinessException("请输入省份！");
            }
            if (query == null || query.equals("")) {
                throw new BusinessException("请输入查询地址！");
            }
            region = URLEncoder.encode(region, "UTF-8");
            query = URLEncoder.encode(query, "UTF-8");
            String url = "https://api.map.baidu.com/place/v2/suggestion?query=" + query + "&region=" + region + "&output=json&ak=" + baiduAK;
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url).openConnection());
            urlConnection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String res;
            StringBuilder sb = new StringBuilder("");
            while ((res = reader.readLine()) != null) {
                sb.append(res.trim());
            }
            reader.close();
            String str = sb.toString();
            JSONObject jsonObject = JSONObject.fromObject(str);
            String status = jsonObject.getString("status");
            if (!status.equals("0")) {
                //log.error("搜索错误。。。");
                throw new BusinessException("搜索失败！");
            } else {
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                if (jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonResult = jsonArray.getJSONObject(i);
                        String name = jsonResult.getString("name");
                        String city = jsonResult.getString("city");
                        String district = jsonResult.getString("district");
                        try {
                            String location = jsonResult.getString("location");
                            JSONObject jsonlocation = JSONObject.fromObject(location);
                            String lng = jsonlocation.getString("lng");
                            String lat = jsonlocation.getString("lat");
                            Monitor monitor = new Monitor();
                            monitor.setName(name);
                            monitor.setLat(lat);
                            monitor.setLng(lng);
                            monitor.setCity(city);
                            monitor.setDistrict(district);
                            list.add(monitor);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BusinessException("没有经纬度信息不做处理！");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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

    /**
     * 驾驶路线规划
     *
     * @param originLat    起点纬度 40.056878
     * @param originLng    起点经度 116.30815
     * @param destLat      终点纬度 39.915285
     * @param destLng      终点经度 116.403857
     * @param mode         导航模式 driving（驾车）、walking（步行）、transit（公交）、riding（骑行）
     * @param waypoints    途经点集合 {[40.056878,116.30815],[39.915285,116.403857]}
     * @param tactics      导航策略 10，不走高速；11、最少时间；12、最短路径
     * @param originRegion 起始点所在城市 北京
     * @param destRegion   终点所在城市   北京
     * @return Map
     * distance 距离 米
     * duration 时间 秒
     * @throws Exception
     */
    public static DirectionDto getDirection(String originLat, String originLng,
                                            String destLat, String destLng, String mode, String[] waypoints,
                                            int tactics, String originRegion, String destRegion) {
        if (originLat == null || originLat.equals("")) {
            throw new BusinessException("请输入起点纬度!");
        } else {
            if (!ChkIntfData.isPositiveDecimal(originLat) || (Float.parseFloat(originLat) <= 0)) {
                throw new BusinessException("请输入正确的起点纬度!");
            }
        }
        if (originLng == null || originLng.equals("")) {
            throw new BusinessException("请输入起点经度!");
        } else {
            if (!ChkIntfData.isPositiveDecimal(originLng) || (Float.parseFloat(originLng) <= 0)) {
                throw new BusinessException("请输入正确的起点经度!");
            }
        }
        if (destLng == null || destLng.equals("")) {
            throw new BusinessException("请输入终点纬度!");
        } else {
            if (!ChkIntfData.isPositiveDecimal(destLng) || (Float.parseFloat(destLng) <= 0)) {
                throw new BusinessException("请输入正确的终点纬度!");
            }
        }
        if (destLat == null || destLat.equals("")) {
            throw new BusinessException("请输入终点经度!");
        } else {
            if (!ChkIntfData.isPositiveDecimal(destLat) || (Float.parseFloat(destLat) <= 0)) {
                throw new BusinessException("请输入正确的终点经度!");
            }
        }
        if (mode == null || mode.equals("")) {
            throw new BusinessException("请输入导航模式!");
        } else {
            if (!mode.equals("driving") && !mode.equals("walking") && !mode.equals("transit") && !mode.equals("riding")) {
                throw new BusinessException("请输入正确的导航模式!");
            }
        }
        String waypointsStr = "";
        if (waypoints != null && waypoints.length > 0) {
            for (int i = 0; i < waypoints.length; i++) {
                String waypoi = (String) waypoints[i];
                String[] way = waypoi.split(",");
                if (!ChkIntfData.isPositiveDecimal(way[0]) || (Float.parseFloat(way[0]) <= 0)) {
                    throw new BusinessException("途经点纬度有错误的数据!");
                }
                if (!ChkIntfData.isPositiveDecimal(way[1]) || (Float.parseFloat(way[1]) <= 0)) {
                    throw new BusinessException("途经点经度有错误的数据!");
                }
                if (i == (waypoints.length - 1)) {
                    waypointsStr += way[0] + "," + way[1];
                } else {
                    waypointsStr += way[0] + "," + way[1] + "|";
                }
            }
        }
        if (tactics <= 0) {
            throw new BusinessException("请输入导航策略!");
        } else {
            if (tactics != 10 && tactics != 11 && tactics != 12) {
                throw new BusinessException("请输入正确的导航策略!");
            }
        }
        if (originRegion == null || originRegion.equals("")) {
            throw new BusinessException("请输入起始点所在城市!");
        }
        if (destRegion == null || destRegion.equals("")) {
            throw new BusinessException("请输入终点所在城市!");
        }
        String url = "https://api.map.baidu.com/direction/v1";
        String param = "mode=driving" +
                "&origin=" + originLat + "," + originLng +
                "&destination=" + destLat + "," + destLng +
                "&mode=" + mode;
        if (!waypointsStr.equals("")) {
            param += "&waypoints=" + waypointsStr;
        }
        param += "&tactics=" + tactics +
                "&origin_region=" + originRegion +
                "&destination_region=" + destRegion +
                "&output=json" +
                "&ak=" + baiduAK;
        //log.info("路线规划地址:"+url+"?"+param);
        String lines = sendGet(url, param);
        Map reqMap = JsonHelper.parseJSON2Map(lines);
        int stats = Integer.parseInt(reqMap.get("status") + "");
        if (stats != 0) {
            //log.info("线路规划异常，错误码："+stats);
            throw new BusinessException("线路规划异常，错误码：" + stats);
        }
        Map resultMap = (Map) reqMap.get("result");
        List<Map> routesList = (List) resultMap.get("routes");
        long distance = 0;
        long duration = 0;
        if (routesList != null && routesList.size() > 0) {
            for (Map routesMap : routesList) {
                //log.info("距离:"+routesMap.get("distance"));
                distance += Long.parseLong(routesMap.get("distance") + "");
                duration += Long.parseLong(routesMap.get("duration") + "");
            }
            //log.info("总距离:"+distance);
        }
        DirectionDto direction = new DirectionDto();
        direction.setDistance(distance);
        direction.setDuration(duration);
        return direction;
    }

    /**
     * http get 请求
     *
     * @param url
     * @param param
     * @return
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            String urlNameString;
            if (org.apache.commons.lang.StringUtils.isNotEmpty(param)) {
                urlNameString = url + (url.indexOf("?") < 0 ? "?" : "&") + param;
            } else {
                urlNameString = url;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            connection = (HttpURLConnection) realUrl.openConnection();
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            result = in.readLine();
        } catch (Exception e) {
            //log.error(e.getMessage(),e);
            throw new BusinessException("发送GET请求出现异常:" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                throw new BusinessException("发送GET请求出现异常:" + ex.getMessage());
            }
        }
        return result;
    }

    /**
     * 根据当前的经纬度获取范围内的经纬度4个点
     * @param lat 经度 23.126007
     * @param lng 纬度 113.423990
     * @param distance  距离 500
     * @return
     */
    public static double[][] getRectangle4Point(double lat, double lng,double distance) {
        double dlng = 2 * Math.asin(Math.sin(distance/(2*EARTH_RADIUS))/Math.cos(Math.toRadians(lat)));
        dlng = Math.toDegrees(dlng);
        double dlat = distance / EARTH_RADIUS;
        dlat = Math.toDegrees(dlat); // # 弧度转换成角度
        double[][] locations = new double[][]{
                {lat + dlat, lng - dlng},//左上角
                {lat + dlat, lng + dlng},//右上角
                {lat - dlat, lng - dlng},//左下角
                {lat - dlat, lng + dlng}
        };//右下角
        return locations;
    }
}
