package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/21
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ScanOrderDriverSwitchVo implements Serializable {
    private Long orderId;
    private Long formerUserId;
    private String formerMileage;
    private String formerMileageFileId;
    private String formerMileageFileUrl;
    /**
     * 备注
     */
    private String formerRemark;
    private Long receiveUserId;
    private String receiveMileage;
    private String receiveMileageId;
    private String receiveMileageUrl;
    /**
     * 备注
     */
    private String receiveRemark;


}
