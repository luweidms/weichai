package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统菜单按钮表
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysMenuBtn extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 菜单编码
     */
    private Long menuId;

    /**
     * 按钮名称
     */
    private String funcName;

    /**
     * 按钮名称（别名）
     */
    private String funcNameAlias;

    /**
     * 状态;默认0(不可用),1(可用)
     */
    private Integer state;

    /**
     * 类型 1页面菜单 2 数据菜单
     */
    private Integer type;

    /**
     * 操作人,对应sys_operator的operator_id
     */
    private Long opId;

    private String remark;

    @TableField(exist = false)
    private boolean isChecked;

}
