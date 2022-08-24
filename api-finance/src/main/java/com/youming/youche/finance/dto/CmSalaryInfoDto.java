package com.youming.youche.finance.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CmSalaryInfoDto implements Serializable {
   private String settleMonth;
   private  String fleetName;
   private List<String> states;
   private String state;
   private Long userId;
   private Integer userType;
}
