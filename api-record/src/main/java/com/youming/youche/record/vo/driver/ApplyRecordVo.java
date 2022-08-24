package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @version:
 * @Title: ApplyRecordVo
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/27 15:42
 * @company:
 */
@Data
public class ApplyRecordVo implements Serializable {
    private Long id;
    private Integer applyType;
    private Integer applyCarUserType;
    private Integer applyVehicleClass;
    private Long busiId;
    private String applyRemark;
    private Long applyFileId;
    private Long applyTenantId;
    private Long beApplyTenantId;
    private Long auditOpId;
    private Date auditDate;
    private String auditRemark;
    private Integer state;
    private int type;
    private int update;
    private String applyPlateNumbers;

}
