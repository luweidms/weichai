package com.youming.youche.finance.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/4/12 13:00
 */
@Data
public class OrderDealInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private Long noPayFinal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finalPianDate;

    private Integer sourceRegion;

    private String sourceRegionName;

    private Integer desRegion;

    private String desRegionName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    private Integer finalSts;

    private String finalStsName;
}
