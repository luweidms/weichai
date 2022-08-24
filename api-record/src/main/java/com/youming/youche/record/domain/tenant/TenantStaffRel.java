package com.youming.youche.record.domain.tenant;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 员工信息
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantStaffRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 用户编号
     */

    private Long userInfoId;

    /**
     * 租户id
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

}
