package com.youming.youche.market.dto.youca;


import com.youming.youche.market.domain.youka.OilCardManagement;
import lombok.Data;

import java.io.Serializable;

@Data
public class OilCardManagementOutDto extends OilCardManagement implements Serializable {
     private  String plateNumber; //车牌号
}
