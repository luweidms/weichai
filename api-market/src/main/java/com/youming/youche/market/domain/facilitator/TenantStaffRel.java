package com.youming.youche.market.domain.facilitator;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 员工信息
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantStaffRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 用户编号
     */
    private Long userInfoId;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 员工姓名
     */
    private String staffName;

    /**
     * 员工工号
     */
    private String employeeNumber;

    /**
     * 职位
     */
    private String staffPosition;

    /**
     * 是否启用（1：启用，2停用）（对应静态数据LOCK_FLAG）
     */
    private Integer lockFlag;

    /**
     * 是否删除，0：已删除，1：未删除
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 用户id
     */
    private Long userId;


}
