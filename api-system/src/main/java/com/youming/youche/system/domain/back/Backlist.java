package com.youming.youche.system.domain.back;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 黑名单
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Backlist extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机
     */
    private String driverPhone;

    /**
     * 身份证号码
     */
    private String identification;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 归属车队
     */
    private Long belongTenantId;

    /**
     * 名单类型（1、车队；2、平台）
     */
    private Integer backType;

    /**
     * 拉黑状态（0、已解除；1、已拉黑）
     */
    private Integer state;

    /**
     * 拉黑原因
     */
    private String reason;

    /**
     * 登记人（发布人）
     */
    private Long createUserId;

    /**
     * 发布车队
     */
    private Long tenantId;

    @TableField(exist = false)
    private String belongTenantName;

    @TableField(exist = false)
    private String createUserName;


}
