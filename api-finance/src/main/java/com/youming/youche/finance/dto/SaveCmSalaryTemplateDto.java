package com.youming.youche.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/4/27
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class SaveCmSalaryTemplateDto implements Serializable {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String channelType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private String fieldCode;
    private String fieldDesc;
    private Integer fieldIndex;
    private Integer isCancel;
    private Integer isDefault;
    private boolean isSelect;
    private String remark;
    private String tableName;
    private Long templateId;
    private Long tenantId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    private Long updateOpId;


}
