package com.youming.youche.cloud.domin.sms;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

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
public class SysSmsTemplate extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     *  模版编号
     */
    private String templateNumber;

    /**
     *  备注
     */
    private String remarks;

    /**
     *  是否执行:0不执行，1执行
     */
    private Integer smsPerform;

    /**
     *  状态 ：0不可用，1可用
     */
    private Integer state;

    /**
     *  模版内容
     */
    private String templateContent;

    /**
     *  模版名称
     */
    private String templateName;

    /**
     *  归属车队
     */
    private Long tenantId;



}
