package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
@Data
public class LineOilQueryInDto implements Serializable {


    private Long lineId ;
    private int type;
    private  Double sourceEand;
    private Double sourceNand;
    private Double desEand;
    private Double desNand;
    private String productName;
    //名称全称
    private String productFullName;
    private int isBill;
    private String orderBy;
    private int oilSelect;//油站选择 -1 全部 1 自有 2 共享
}
