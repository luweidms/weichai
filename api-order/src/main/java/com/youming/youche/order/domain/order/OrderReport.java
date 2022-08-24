package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *订单报备表
 * </p>
 *
 * @author wuhao
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderReport extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 报备图片id
     */
    private String imgIds;

    /**
     * 报备图片路径
     */
    private String imgUrls;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 报备描述
     */
    private String reportDesc;

    /**
     * 报备类型
     */
    private Integer reportType;

    /**
     * 报备类型
     */
    @TableField(exist = false)
    private String reportTypeStr;

    /**
     * 报备人id
     */
    private Long reportUserId;

    /**
     * 报备人姓名
     */
    private String reportUserName;

    /**
     * 报备人手机号
     */
    private String reportUserPhone;

    /**
     * 租户id
     */
    private Long tenantId;


}
