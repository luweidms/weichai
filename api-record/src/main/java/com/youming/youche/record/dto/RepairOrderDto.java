package com.youming.youche.record.dto;

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
    private String contractName; // 司机姓名
    private String contractMobile; // 司机手机号
    private String carNo;// 车牌号
    private Long vehicleCode; // 车辆编号
    private String workType; // 类型
    private Date scrStartTime; // 开始时间
    private Date scrEndTime; // 结束时间
    private String items; //项目
    private Long flowId; // 主键
    private String picFileIds; // 图片
    private String remark; // 备注

}
