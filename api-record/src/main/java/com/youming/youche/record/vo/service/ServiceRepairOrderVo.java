package com.youming.youche.record.vo.service;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @description 车辆维保查询
 * @date 2022/1/20 11:10
 */
@Data
public class ServiceRepairOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    private Integer orderStatus;
    /**
     * 维保类型
     */
    private String workType;
    /**
     * 司机姓名
     */
    private String contractName;
    /**
     * 司机手机号
     */
    private String contractMobile;
    /**
     * 网点名称
     */
    private String shopName;
    /**
     * 车牌号
     */
    private String carNo;
    /**
     * 计划开始时间
     */
    private String scrStartTime;
    /**
     * 计划结束时间
     */
    private String scrEndTime;
    /**
     * 维保开始
     */
    private String repairStartTime;
    /**
     * 维保结束
     */
    private String repairEndTime;
    /**
     * 单号
     */
    private String orderCode;
    /**
     * 单号
     */
    private String orderSn;
    private Long tenantId;
    private String tenantName;

    /**
     * 申请人
     */
    private String opName;

}
