package com.youming.youche.system.domain.audit;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: AuditNodeOut
 * @Package: com.youming.youche.system.domain.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 10:10
 * @company:
 */
@Data
public class AuditNodeOut implements Serializable {
    private Long auditId;
    private String auditCode;
    private Long parentNodeId;
    private Long nodeId;
    private Integer version;

}
