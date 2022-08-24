package com.youming.youche.record.vo.service;

import lombok.Data;

import java.io.Serializable;

/**
 * 车辆维保插入修改入参
 *
 * @date 2022/1/11 13:02
 */
@Data
public class ServiceRepairOrderIUVo implements Serializable {

    private static final long serialVersionUID = 1L;

//    /**
//     * 维保类型
//     */
//    private String workTypeName;
    /**
     * 工单类型
     */
    private String workType;
    //    /**
//     * 申请人
//     */
//    private String userName;
//    /**
//     * 联系号码
//     */
//    private String userBillId;
    private Long id;
//    /**
//     * 申请时间
//     */
//    private String createDate;
    /**
     * 车牌号
     */
    private String carNo;
    /**
     * 司机姓名
     */
    private String contractName;
    /**
     * 司机号码
     */
    private String contractMobile;
    private Long vehicleCode;
    /**
     * 计划始末时间
     */
    private String scrStartTime;
    private String scrEndTime;
    /**
     * 申请原因
     */
    private String remark;
    /**
     * 保养项目
     */
    private String items;
    /**
     * 上传图片
     */
    private String picFileIds;
}
