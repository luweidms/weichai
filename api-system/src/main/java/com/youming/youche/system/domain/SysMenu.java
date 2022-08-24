package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统菜单表
 * </p>
 *
 * @author Terry
 * @since 2022-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysMenu extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 菜单名称
	 */
	private String menuName;

	/**
	 * 菜单名称（别名）（菜单权限的展示）
	 */
	private String menuNameAlias;

	/**
	 * 菜单名称 中文
	 */
	private String menuIconExtend;

	/**
	 * 上级菜单编号,-1代表一级菜单
	 */
	private Long parentId;

	/**
	 * 菜单路径
	 */
	private String menuPath;

	/**
	 * 菜单序号
	 */
	private Integer menuSeq;

	/**
	 * 图标
	 */
	private String menuIcon;

	/**
	 * 状态;默认0(不可用),1(可用)
	 */
	private Integer state;

	/**
	 * 类型 1页面菜单 2 数据菜单
	 */
	private Integer type;

	private String remark;

	@TableField(exist = false)
	private List<SysMenu> children = new ArrayList<>();
	@TableField(exist = false)
	private List<SysMenuBtn> buttons = new ArrayList<>();

	@TableField(exist = false)
	private boolean isChecked;

}
