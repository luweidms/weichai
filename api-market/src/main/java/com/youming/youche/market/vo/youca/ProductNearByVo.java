package com.youming.youche.market.vo.youca;

import com.youming.youche.market.dto.youca.ProductNearByOutDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductNearByVo implements Serializable {

    private static final long serialVersionUID = 1424612684418124863L;

    private  Long oilBalance;
    private  Double oilBalanceDouble;
    private List<ProductNearByOutDto> outList;

    private  Integer repairCount;
}
