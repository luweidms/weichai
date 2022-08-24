package com.youming.youche.record.vo.violation;

import lombok.Data;

import java.io.Serializable;

/**
 * 车辆违章回参入参
 *
 * @date 2022/1/7 15:20
 */
@Data
public class ViolationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String plateNumber; // 车牌号码
    private String linkMan; //  司机名称
    private String linkmanPhone; // 司机手机
    private String startTime; // 违章开始时间
    private String endTime; // 违章结束时间
    private String violationCity; // 违章城市
    private String peccancyCode; // 违章代码
    private String publicAndPrivateType; //  公私类型
    private String categoryId; // 违章类型 （此处传入2）
    private String recordId; // 违章id
    private String violationWritNo; // 违章文书号
}
