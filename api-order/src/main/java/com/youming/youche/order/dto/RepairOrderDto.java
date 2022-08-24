package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: luona
 * @date: 2022/5/23
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class RepairOrderDto implements Serializable {
    private String contactName;
    private String contactMobile;
    private String carNo;
    private Long vehicleCode;
    private String workType;
    private Date scrStartTime;
    private Date scrEndTime;
    private String items;
    /**
     * 流水号
     */
    private Long flowId;
    private String picFileIds;
}
