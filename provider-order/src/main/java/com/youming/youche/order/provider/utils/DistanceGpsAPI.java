package com.youming.youche.order.provider.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class DistanceGpsAPI {

    private static final String BAIDU_AK = "xNwsarM3juMLh86MtPAkpB7oWi2h9lE3";

    /**
     * 返回输入地址的经纬度坐标 key lng(经度),lat(纬度)
     */
    public static Map<String, String> getLatitude(String address) {
        try {
            // 将地址转换成utf-8的16进制
            address = URLEncoder.encode(address, "UTF-8");
            URL resjson = new URL("http://api.map.baidu.com/geocoder?address="
                    + address + "&output=json&key=" + BAIDU_AK);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    resjson.openStream()));
            String res;
            StringBuilder sb = new StringBuilder("");
            while ((res = in.readLine()) != null) {
                sb.append(res.trim());
            }
            in.close();
            String str = sb.toString();
            System.out.println("return json:" + str);
            if(str!=null&&!str.equals("")){
                Map<String, String> map = null;
                int lngStart = str.indexOf("lng\":");
                int lngEnd = str.indexOf(",\"lat");
                int latEnd = str.indexOf("},\"precise");
                if (lngStart > 0 && lngEnd > 0 && latEnd > 0) {
                    String lng = str.substring(lngStart + 5, lngEnd);
                    String lat = str.substring(lngEnd + 7, latEnd);
                    map = new HashMap<String, String>();
                    map.put("lng", lng);
                    map.put("lat", lat);
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //将返回数据改成double类型单位改成千米
    private static double format(String temp) {
        Double one=Double.parseDouble(temp);
        return (one/1000);
    }

    //返回两地距离
    public static double backDis(Map<String, String> A,Map<String, String> B) {
        try {
            URL resjson = new URL
                    ("http://api.map.baidu.com/routematrix/v2/driving?output=json&origins="+A.get("lat")+","+A.get("lng")+"&destinations="+B.get("lat")+","+B.get("lng")+"&ak="+BAIDU_AK);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    resjson.openStream()));
            String res;
            StringBuilder sb = new StringBuilder("");
            while ((res = in.readLine()) != null) {
                sb.append(res.trim());
            }
            in.close();
            String str = sb.toString();
            System.out.println("return json:" + str);
            if(str!=null&&!str.equals("")){
                double temp=0;
                int disStart = str.indexOf("value\":");
                int disEnd = str.indexOf("},\"duration");
                if (disStart > 0  && disEnd > 0) {
                    String dis = str.substring(disStart + 7, disEnd);
                    temp=format(dis);
                    return temp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Double) null;
    }

    public static void main(String args[])
    {
        double temp=backDis(getLatitude("苏州姑苏区大观花园北区"), getLatitude("苏州工业园区葑亭大道666号"));
        System.out.print("temp===="+temp);
    }
}
