package com.youming.youche.system.domain.audit;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: AuditNodeUser
 * @Package: com.youming.youche.system.domain.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 9:51
 * @company:
 */
@Data
public class AuditNodeUser implements Serializable {
    private Long userId;
    private String userName;
    private String userBill;
    private Long nodeId;
    private String userTypeName;
}
