/**  
 * @Title Geocoder.java
 * @Package com.hsae.hbase.geography
 * @Description TODO(用一句话描述该文件做什么)
 * @author 韩欣宇
 * @date: 2016年3月4日 下午3:42:04
 * @company 上海势航网络科技有限公司
 * @version V1.0  
 */
package com.youming.youche.util;

import java.math.BigDecimal;

/**
 * @ClassName Geocoder
 * @Description 地址解析，提供将地址信息转换为坐标点信息的服务
 * @author 韩欣宇
 * @company 上海势航网络科技有限公司
 * @date 2016年3月4日 下午3:42:04
 */
public class Geocoder {

	private static int GEO_SCALE = 6;

	//
	// Krasovsky 1940
	//
	// a = 6378245.0, 1/f = 298.3
	// b = a * (1 - f)
	// ee = (a^2 - b^2) / a^2;
	private static double a = 6378245.0;
	private static double ee = 0.00669342162296594323;

	/**
	 * @Title transform
	 * @Description World Geodetic System ==> Mars Geodetic System
	 * @param wgLat
	 * @param wgLon
	 * @return double[]{mgLon,mgLat}
	 * @author 韩欣宇
	 * @date 2016年3月4日 下午3:30:01
	 */
	public static double[] transformLngLat(double wgLon, double wgLat) {
		double mgLat;
		double mgLon;
		if (outOfChina(wgLat, wgLon)) {
			mgLat = wgLat;
			mgLon = wgLon;
			return new double[] { new BigDecimal(mgLon).setScale(GEO_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue(),
					new BigDecimal(mgLat).setScale(GEO_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() };
		}
		double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
		double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
		double radLat = wgLat / 180.0 * Math.PI;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
		mgLat = wgLat + dLat;
		mgLon = wgLon + dLon;
		return new double[] { new BigDecimal(mgLon).setScale(GEO_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue(),
				new BigDecimal(mgLat).setScale(GEO_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue() };
	}

	private static boolean outOfChina(double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}

	private static double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
		return ret;
	}
	
	/**
	 * 火星坐标转百度坐标
	* @Title: gcj02_To_Bd09
	* @Description: 火星坐标转百度坐标
	* @param @param gg_lon 火星坐标经度 
	* @param @param gg_lat 火星坐标纬度
	* @param @return    设定文件
	* @return double[bd_lon,bd_lat]    返回类型
	* @author: 陈梁
	* @date 2019年3月22日 下午12:57:08
	* @throws
	 */
	public static double[] gcj02_To_Bd09(double gg_lon, double gg_lat) {
		double x = gg_lon;
		double y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI * 3000 / 180);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI * 3000 / 180);
		double bd_lon = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new double[] { bd_lon, bd_lat };
	}

	/**
	 * WGS坐标 转换 百度坐标
	* @Title: wgs84_To_Bd09
	* @Description: WGS坐标 转换 百度坐标
	* @param @param wgs_lon wgs经度
	* @param @param wgs_lat wgs纬度
	* @param @return    设定文件
	* @return double[]{bdLon,bdLat}    返回类型
	* @author: 陈梁
	* @date 2019年3月22日 下午12:53:09
	* @throws
	 */
	public static double[] wgs84_To_Bd09(double wgs_lon, double wgs_lat) {
		double[] gcj = transformLngLat(wgs_lon, wgs_lat);
		double gcj_lon = gcj[0];
		double gcj_lat = gcj[1];
		double[] bdLocation = gcj02_To_Bd09(gcj_lon, gcj_lat);
		return new double[] { bdLocation[0], bdLocation[1] };
	}
	
	/**
	 * WGS坐标 转换 百度坐标
	* @Title: wgs84_To_Bd09
	* @Description: WGS坐标 转换 百度坐标
	* @param @param wgs_lon wgs经度
	* @param @param wgs_lat wgs纬度
	* @param @return    设定文件
	* @return double[]{bdLon,bdLat}    返回类型
	* @author: 陈梁
	* @date 2019年3月22日 下午12:53:09
	* @throws
	 */
	public static double[] wgs84_To_Bd092(double wgs_lon, double wgs_lat) {
		double[] gcj = transformLngLat(wgs_lon, wgs_lat);
		double gcj_lon = gcj[0];
		double gcj_lat = gcj[1];
		double radius = Math.sqrt(gcj_lat * gcj_lat + gcj_lon * gcj_lon);
		double phase = Math.atan2(gcj_lat, gcj_lon);
		double fixedRadius = radius + 2e-5 * Math.sin(gcj_lat * Math.PI * 3000 / 180);
		double fixedPhase = phase + 3e-6 * Math.cos(gcj_lon * Math.PI * 3000 / 180);
		double turnBackX = fixedRadius * Math.cos(fixedPhase) + 0.0065;
		double turnBackY = fixedRadius * Math.sin(fixedPhase) + 0.006;
		return new double[] { turnBackX, turnBackY };
	}

}
