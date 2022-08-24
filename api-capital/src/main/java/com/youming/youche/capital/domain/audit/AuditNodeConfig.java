package com.youming.youche.capital.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 节点配置表
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditNodeConfig extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 审核表的主键
     */
    private Long auditId;

    /**
     * 上个节点的id
     */
    private Long parentNodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 租户
     */
    private Long tenantId;


}
