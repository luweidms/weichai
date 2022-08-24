package com.youming.youche.market.vo.youca;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceProductOutVo  implements Serializable {

    private static final long serialVersionUID = 8855125601172039369L;
    //查询附近油站 接口编码：40000 入参
    private String latitude ; // 纬度
    private String longitude; // 经度
    private Long cityId;
    private Integer locationType;
    private Long oilCardType;

    private  Long amount;
    private Long tenantId;
    private  Long userId;
    private  Boolean isShare;

    //附近维修站查询 40024

    private  Long productPicId;


}
