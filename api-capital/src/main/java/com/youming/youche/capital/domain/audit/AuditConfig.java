package com.youming.youche.capital.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 审核配置主表
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditConfig extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 审核业务编码
     */
    private String auditCode;

    /**
     * 工作流id
     */
    private String wfId;

    /**
     * 业务名称
     */
    private String busiName;

    /**
     * 默认节点数
     */
    private Integer defaultNodeNum;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 回调的类
     */
    private String callback;

    /**
     * 创建人
     */
    private Long opId;

    /**
     * 业务类型（1：基础资料，2：业务审核）
     */
    private Integer busiType;

    /**
     * 节点回调方法
     */
    private String nodeCallback;


}
