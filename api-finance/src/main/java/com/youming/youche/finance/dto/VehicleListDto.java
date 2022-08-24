package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VehicleListDto implements Serializable {
    private String plateNumberDef; // 车牌号
    private List canSelPlateNumbers; //可以选择车牌号码s
}
