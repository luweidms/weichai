package com.youming.youche.capital.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 审核节点实例表
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditNodeInst extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 审核配置表主键
     */
    private Long auditId;

    /**
     * 审核的业务编码
     */
    private String auditCode;

    /**
     * 节点的主键
     */
    private Long nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 操作审核的人id
     */
    private Long auditManId;

    /**
     * 审核状态:1表示在审核的流程中，0表示审核流程结束
     */
    private Integer status;

    /**
     * 审核备注
     */
    private String remark;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 审核时间
     */
    private LocalDateTime auditDate;

    /**
     * 审核结果:0 待审核，1 审核通过 2 审核不通过
     */
    private Integer auditResult;

    /**
     * 审核流程过程的数据
     */
    private String paramsMap;

    /**
     * 业务主键
     */
    private Long busiId;

    /**
     * 日志的类型
     */
    private Integer logBusiCode;

    /**
     * 审核批次
     */
    private String auditBatch;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 规则版本号
     */
    private Integer ruleVersion;

    /**
     * 节点的序号
     */
    private Integer nodeIndex;


}
