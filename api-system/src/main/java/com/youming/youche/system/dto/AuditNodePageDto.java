package com.youming.youche.system.dto;

import com.youming.youche.system.domain.audit.AuditNodeUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @version:
 * @Title: AuditNodePageDto
 * @Package: com.youming.youche.system.dto
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 9:50
 * @company:
 */
@Data
public class AuditNodePageDto implements Serializable {
    private Long nodeId;
    private Long parentNodeId;
    private String auditCode;
    private List<AuditNodeUser> list;
}
