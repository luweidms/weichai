package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class LineOilQueryDto implements Serializable {
    private Long lineId; // 线路ID
    private Integer type; // 1.全部油站 2.推荐油站 3.临时线路
    private Double sourceEand; // 始发地经度
    private Double sourceNand; // 始发地纬度
    private Double desEand; // 目的地经度
    private Double desNand; // 目的地纬度
    private String productName; // 产品名称
    private String productFullName; // 名称全称
    private Integer isBill; // 是否开票（1.是、2.否）
    private String orderBy;
    private Integer oilSelect;//油站选择 -1 全部 1 自有 2 共享

}
