package com.youming.youche.record.dto.service;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @description 车辆维保修改回显
 * @date 2022/1/21 16:31
 */
@Data
public class ServiceRepairOrderUpdateEchoDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    // 工单类型，’GHCWX’-维修，’GHCBY’-保养
    private String workType;
    private String workTypeName;
    // 申请时间
    private String createDate;
    // 操作人id
    private Long opId;
    // 申请人
    private String opName;
    // 联系号码
    private String opPhone;
    // 车牌号
    private String carNo;
    // 司机姓名
    private String contractName;
    // 司机号码
    private String contractMobile;
    // 车牌编号
    private Long vehicleCode;
    // 上次维保里程
    private Integer maintenanceDis;
    // 上次维保时间
    private String lastMaintenanceDate;
    // 计划开始时间
    private String scrStartTime;
    // 计划结束时间
    private String scrEndTime;
    // 项目
    private String items;
    // 申请原因
    private String remark;
    // 图片信息
    private List<Map> repairOrderPicList;
}
