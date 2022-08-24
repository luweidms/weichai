package com.youming.youche.cloud.domin.sms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短信通知配置
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MsgNotifySetting extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Long tenantId;

    /**
     * 通知收款人
     */
    private Boolean notifyReceiver;

    /**
     * 通知车队
     */
    private Boolean notifyTenant;

    /**
     * 配置通知车队的电话，逗号隔开
     */
    private String phone;

    private Long settingId;


}
