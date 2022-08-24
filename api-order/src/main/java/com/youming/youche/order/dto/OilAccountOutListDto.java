package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OilAccountOutListDto implements Serializable {


    private static final long serialVersionUID = -6993417819111690397L;

    /*
     *
     */
    private List<OilAccountOutDto> oaoList;
    /*
     * 订单油
     */
    private List<OrderOilSource> oosList;
    /*
     * 充值油
     */
    private List<RechargeOilSource> rosList;
}
