package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoAuditInfovO
 * @Package: com.youming.youche.record.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/24 13:43
 * @company:
 */
@Data
public class DoAuditInfoVo implements Serializable {

    private Long vehicleCode; // 车辆的ID

    private Integer authState; // 是否通过

    private String auditContent; // 审核原因

}
