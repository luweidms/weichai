package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class VehiclesDto implements Serializable {
    private Set<String> vehicles;
}
