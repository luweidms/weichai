package com.youming.youche.capital.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date:2022/1/10
 */
@Data
public class AuditNodeRuleConfigVo implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 审核id
     */
    private Long auditId;
    /**
     * 规则备注
     */
    private String ruleTips;
    /**
     * 规则类型
     */
    private Integer ruleType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 审核类型
     */
    private String targetObj;
    /**
     * 操作人Id
     */
    private Long opId;
    /**
     * 版本
     */
    private Integer version;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 规则key
     */
    private String ruleKey;
    /**
     * 规则编码
     */
    private String ruleCode;
    /**
     * 规则value
     */
    private String ruleValue;
}
