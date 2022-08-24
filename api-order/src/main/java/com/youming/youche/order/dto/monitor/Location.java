package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 地理位置
 *
 * @author hp
 * @date 2018-06-20
 */
@Data
public class Location implements Serializable {

    private static final long serialVersionUID = -1L;

    //经度
    private double longitude;
    //维度
    private double latitude;
    //时间
    private long time;

    private String timeStr;

    public Location() {
    }

    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location(double longitude, double latitude, long time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }
}