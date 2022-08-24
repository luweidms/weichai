package com.youming.youche.cloud.domin.sms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysSmsSend extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 接收号码
     */
    private String billId;
    /**
     * 拓展号
     */
    private String expId;
    /**
     * 短信内容MD5值
     */
    private String md5;
    /**
     * 业务Id
     */
    private String objId;
    /**
     * 业务类型
     */
    private String objType;
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    /**
     * 发送标示：1已发送 ，0未发送
     */
    private Integer sendFlag;
    /**
     * 短信内容
     */
    private String smsContent;
    /**
     * 短信类型
     */
    private Integer smsType;
    /**
     * 短信来源：1手机，PC
     */
    private Integer srcType;
    /**
     * 模板Id
     */
    private Long templateId;
    /**
     * 车队Id
     */
    private Long tenantId;
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 参数Map
     */
    @TableField(exist = false)
    private Map paramMap;

    /**
     * 模版编号
     */
    @TableField(exist = false)
    private String templateNumber;


}
