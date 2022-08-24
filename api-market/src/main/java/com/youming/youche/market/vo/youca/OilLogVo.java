package com.youming.youche.market.vo.youca;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilLogVo  implements Serializable {
    private  String logDateBegin ;
    private  String logDateEnd;
    private  String orderId;
    private  String plateNumber;
    private  String carDriverMan;
    private  Long   cardId;
}
