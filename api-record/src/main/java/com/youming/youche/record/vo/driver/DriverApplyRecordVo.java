package com.youming.youche.record.vo.driver;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version:
 * @Title: ApplyRecordVo
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/26 16:01
 * @company:
 */
@Data
public class DriverApplyRecordVo implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 司机账号
     */
    private String mobilePhone;

    /**
     * 用户头像
     */
    private String userPrice;

    /**
     * 司机类型
     */
    private Integer applyCarUserType;

    /**
     * 司机类型名称
     */
    private String applyCarUserTypeName;

    /**
     * 邀请司机的车队ID
     */
    private Long beApplyTenantId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 审核状态
     */
    private Integer state;

    /**
     * 审核状态名称
     */
    private String stateName;

    /**
     * 邀请说明
     */
    private String applyRemark;

    /**
     * 邀请附件
     */
    private Long applyFile;

    /**
     * 审核意见
     */
    private String auditRemark;

    /**
     * 新司机ID
     */
    private Long newApplyId;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDate;

}
