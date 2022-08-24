package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class QueryOrderLineNodeListVo implements Serializable {


    private static final long serialVersionUID = -3728230793907566589L;
    /**
     * 起始点
     */
    private Integer sourceRegion;
    /**
     * 到达点
     */
    private Integer desRegion;
    /**
     * 到达时限
     */
    private Float arrive;

    /**
     * 经度
     */
    private String nand;
    /**
     * 目的地经度
     */
    private String nandDes;

    /**
     * 纬度
     */
    private String eand;
    /**
     * 目的地纬度
     */
    private String eandDes;
    /**
     * 线路详情
     */
    private String detailLine;
    /**
     * 实际出发时间
     */
    private LocalDateTime carStartDate;
    /**
     * 实际到达时间
     */
    private LocalDateTime carArriveDate;
}
