package com.youming.youche.finance.dto.order;

import com.youming.youche.market.domain.etc.CmEtcInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/14 18:56
 */
@Data
public class CalculatedEtcFeeDto implements Serializable {

    private List<CmEtcInfo> items;

    private Long money;

    private Integer totalNum;

    private Integer page;

    private Integer count;

    private Boolean hasNext;
}
