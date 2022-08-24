package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderLineDto implements Serializable {

    private static final long serialVersionUID = 6039822694859235052L;

    /**
     * 地址纬度
     */
    private String nand;
    /**
     * 地址经度
     */
    private String eand;
    /**
     *
     */
    private Integer lineType;

}
