package com.youming.youche.market.vo.etc;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/15 9:30
 */
@Data
public class CalculatedEtcFeeVo implements Serializable {

    private String receiverPhone;

    private Long receiverUserId;

    private String plateNumber;

    private Long tenantId;

    private List<String> months;
}
