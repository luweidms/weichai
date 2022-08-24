package com.youming.youche.record.domain.violation;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 违章报价
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ViolationQuotation extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long quotationId;

    /**
     * 违章记录ID
     */
    private Long recordId;

    /**
     * 服务商id
     */
    private Long serviceUserId;

    /**
     * 服务商代理费
     */
    private Long cooperFree;

    /**
     * 平台代理费
     */
    private Long ptCooperFree;

    /**
     * 服务商的违章ID
     */
    private String violationId;

    /**
     * 是否可代办 （1、可以代办； 0、 不可以代办）
     */
    private Integer processStatus;

    /**
     * 不可代办原因
     */
    private String processMsg;

    /**
     * 服务商代办规则号
     */
    private String freeCode;

    /**
     * 总报价
     */
    private Long totalQuotation;

    /**
     * 服务商代理费+平台代理费
     */
    private Long totalCooperFree;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 更新时间
     */
    private String updateDate;

    /**
     * 操作人ID
     */
    private Long opId;

    /**
     * 1、询价中；2、询价失效；3、不处理；4、已报价
     */
    private Integer quotationState;


}
