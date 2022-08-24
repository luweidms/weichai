package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.domain.facilitator.ServiceProduct;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceProdcutByNameVo implements Serializable {
    private Integer info;
    private ServiceProduct serviceProduct;
}
