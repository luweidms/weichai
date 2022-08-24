package com.youming.youche.cloud.domin.sms;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysSmsLog extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 手机号码
     */
    private String billId;

    /**
     * 发送时间
     */
    private LocalDateTime sendData;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 短信表id
     */
    private Long smsId;

    /**
     * 模版id
     */
    private Long templateId;

    /**
     * 车队id
     */
    private Long tenantId;


}
