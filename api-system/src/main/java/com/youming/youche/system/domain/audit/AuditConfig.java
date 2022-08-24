package com.youming.youche.system.domain.audit;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @version:
 * @Title: AuditConfig
 * @Package: com.youming.youche.system.domain.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 17:32
 * @company:
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AuditConfig extends BaseDomain {

//    /**
//     * 主键
//     */
//    @Id // 主键
//    @TableId(value = "AUDIT_ID", type = IdType.AUTO)
//    @Column(name = "AUDIT_ID")
//    private Long auditId;

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
