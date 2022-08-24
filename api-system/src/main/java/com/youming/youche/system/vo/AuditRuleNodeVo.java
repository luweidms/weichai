package com.youming.youche.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: AuditRuleNodeVo
 * @Package: com.youming.youche.system.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/16 10:39
 * @company:
 */
@Data
public class AuditRuleNodeVo implements Serializable {
    private Long nodeId;
    private Long parentNodeId;
    private String auditCode;
    private String targetObjId;
    private Integer targetObjType;
    private String exceedGuidePrice;
    private Boolean isHigherPrepayment;
    private Boolean isHigherOil;
    private Boolean isLowerOil;
    private Boolean isHigherEtc;
    private Boolean isLowerEtc;
}
