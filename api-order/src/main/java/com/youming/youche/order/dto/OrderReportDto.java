package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderReportDto implements Serializable {


    private Long id;
    private Integer reportType;
    private String reportDesc;
    private String imgIds;
    private String imgUrls;
    private Long orderId;
    private Long reportUserId;
    private String reportUserName;
    private String reportUserPhone;
    private String reportTypeName;

    private String[] imgUrlArr;
    private LocalDateTime createDate;
}
