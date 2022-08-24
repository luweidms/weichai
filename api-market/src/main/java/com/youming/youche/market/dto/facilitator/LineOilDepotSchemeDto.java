package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class LineOilDepotSchemeDto  implements Serializable {
    private Double distance;
    private Long oilId;
    private String oilName;
    private String oilPhone;
    private Double oilPrice;
    private String address;
    private String serviceName;
    private String eand;
    private String nand;
}
