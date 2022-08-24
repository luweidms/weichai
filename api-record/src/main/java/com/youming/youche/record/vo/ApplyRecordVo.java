package com.youming.youche.record.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version:
 * @Title: ApplyRecordVo
 * @Package: com.youming.youche.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/7 16:12
 * @company:
 */
@Data
public class ApplyRecordVo implements Serializable {

    private Long id;

    private String mobilePhone;

    private String linkman;

    private Integer applyCarUserType;

    private String applyCarUserTypeName;

    private Long beApplyTenantId;

    private String attachTenantName;

    private String attachTennantLinkPhone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    private Integer state;

    private Integer readState;

    private String auditRemark;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDate;

    private Long newApplyId;

    private String stateName;

    private Integer auditFlg;

}
