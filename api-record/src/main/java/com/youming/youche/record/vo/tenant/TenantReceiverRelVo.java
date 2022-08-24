package com.youming.youche.record.vo.tenant;

import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TenantReceiverRelVo implements Serializable {
    private String  remark; // 备注
    private String  receiverName; // 收款人名称
    private String  linkman; // 联系人名称
    private String  mobilePhone; // 手机号
    private String  bindCard; // 账户编号
}
