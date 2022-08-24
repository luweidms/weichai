package com.youming.youche.system.dto;

import com.youming.youche.system.constant.SysOperLogConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
@Deprecated
public class AuditDto implements Serializable {


    private static final long serialVersionUID = 7868730060890919289L;


    private String auditCode;
    private Long busiId;
    private SysOperLogConst.BusiCode busiCode;
    private Map<String, Object> params;
    private String accessToken;
    private Long tenantId;
}
