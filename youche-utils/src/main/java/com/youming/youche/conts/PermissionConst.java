package com.youming.youche.conts;

/**
 * <p>
 * 角色权限常量
 * </p>
 *
 * @author Terry
 * @since 2021-12-30
 */
public class PermissionConst {

	/** 菜单权限 */
	public static final Integer PERMISSION_MENU = 1;

	/** 操作访问权限 */
	public static final Integer PERMISSION_URL = 2;

	/** 图片访问权限 */
	public static final Integer PERMISSION_IMAGE = 3;

	/** 文件访问权限 */
	public static final Integer PERMISSION_FILE = 4;


	/**
	 * 数据权限
	 */
	public static class MENU_BTN_ID{
		/**
		 * 所有数据权限ENTITY_ID
		 */
		public static final long ALL_DATA = 630;
		/**
		 * 订单收入权限
		 */
		public static final long ORDER_INCOME = 631;
		/**
		 * 订单成本权限
		 */
		public static final long ORDER_COST = 632;
		/**
		 * 线路收入权限
		 */
		public static final long LINE_INCOME = 633;
		/**
		 * 线路成本权限
		 */
		public static final long LINE_COST = 634;
	}
}
