package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date:2022/1/10
 */
@Data
public class AuditNodeRuleConfigVo implements Serializable {

    private Long id;

    private String ruleName;

    private Long auditId;

    private String ruleTips;

    private Integer ruleType;

    private Date createTime;

    private String targetObj;

    private Long opId;

    private Integer version;

    private Date updateTime;

    private String ruleKey;

    private String ruleCode;

    private String ruleValue;
}
