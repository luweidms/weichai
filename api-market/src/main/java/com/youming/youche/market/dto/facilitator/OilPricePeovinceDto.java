package com.youming.youche.market.dto.facilitator;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class OilPricePeovinceDto implements Serializable {

    /**
     * 油价
     */
    @ExcelProperty(index= 1)
    private Double oilPrice;

    /**
     * 省份名
     */
    @ExcelProperty(index = 0)
    private String provinceName;
}
