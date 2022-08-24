package com.youming.youche.system.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@RequiredArgsConstructor(staticName = "of")
@Accessors(chain = true)
public class AuditCallbackDto implements Serializable {


    private static final long serialVersionUID = -334583010554498413L;

    private Long busiId;
    private Integer result;
    private String desc;
    private Map<String, Object> paramsMap;
    private String callback;
    private String token;
    private Integer type;
    /**
     * 是否走审核流程，true走流程
     * */
    private Boolean isAudit = false;

    private Boolean isNext = false;
}
