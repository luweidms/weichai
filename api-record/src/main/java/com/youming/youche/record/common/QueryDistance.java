package com.youming.youche.record.common;

import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 向子俊
 * @version 1.0.0
 * @ClassName QueryDistance.java
 * @Description TODO计算距离入参
 * @createTime 2022年02月23日 00:08:00
 */
@Data
public class QueryDistance implements Serializable {

    /**
     * 出发地纬度
     */
    String originLat;

    /**
     * 出发地经度
     */
    String originLng;

    /**
     * 目的地纬度
     */
    String destLat;

    /**
     * 目的地经度
     */
    String destLng;

    /**
     * 出发地地址
     */
    String originRegion;

    /**
     * 目的地地址
     */
    String destRegion;

    /**
     * 途径站点
     */
    List<CmCustomerLineSubway> lineSubwayList;
}
