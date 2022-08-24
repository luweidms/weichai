package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@Data
@Accessors(chain = true)
public class SysRoleOperRel implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer roleOperId;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 最近修改时间
     */
    private LocalDateTime lastModifyDate;
    /**
     * 操作人
     */
    private Long lastModifyOperatorId;
    /**
     * 操作员表主键
     */
    private Long operatorId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 车辆和租户
     */
    @TableId(value = "rel_id", type = IdType.AUTO)
    private Long relId;
    /**
     * 操作時間
     */
    private LocalDateTime opDate;
    /**
     * 操作人
     */
    private Long opId;
    /**
     * 部门id
     */
    private Long orgId;
    /**
     * 用户id
     */
    private Long userId;


}
