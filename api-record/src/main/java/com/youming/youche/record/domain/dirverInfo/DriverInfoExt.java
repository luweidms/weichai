package com.youming.youche.record.domain.dirverInfo;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 司机信息扩展表
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DriverInfoExt extends BaseDomain {


    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 是否路哥认证（0、否；1、是）
     */
    private Boolean luGeAuthState;

    /**
     * 路哥认证状态修改时间
     */
    private LocalDateTime lgUpdateDate;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 备注、描述
     */
    private String remark;

    /**
     * 路哥处理状态(0、未处理；1、已处理)
     */
    private Integer processState;

}
