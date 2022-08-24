package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 司机信息扩展表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DriverInfoExt extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 用户编号
     */
    @TableField("USER_ID")
    private Long userId;

    /**
     * 是否路哥认证（0、否；1、是）
     */
    @TableField("LU_GE_AUTH_STATE")
    private Boolean luGeAuthState;

    /**
     * 注意
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 路哥处理状态(0、未处理；1、已处理)
     */
    @TableField("PROCESS_STATE")
    private Integer processState;


}
