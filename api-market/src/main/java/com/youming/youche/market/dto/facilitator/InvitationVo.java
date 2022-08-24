package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InvitationVo implements Serializable {
    private String tenantName; // 租户姓名
    private List<Integer> cooperationType; // 合作类型 1：初次合作，2：修改合作'
    private List<Integer> authState; // 审核结果（1.待审核、2.审核通过、3.审核未通过）
    private String linkPhone; // 手机号码
    private String linkman; // 租户联系人
}
