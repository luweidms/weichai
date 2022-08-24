package com.youming.youche.finance.commons;

public enum CapitalResponseCode {

	// -----------------------------------参数错误：10001-19999 start

	// -----------------------------------参数错误：10001-19999 end

	// -----------------------------------用户错误：20001-29999 start

	// -----------------------------------用户错误：20001-29999 end
	// -----------------------------------业务错误：30001-39999 start

	// -----------------------------------业务错误：30001-39999 end

	// -----------------------------------系统错误：40001-49999 start

	// -----------------------------------系统错误：40001-49999 end

	// -----------------------------------数据错误：50001-59999 start

	// -----------------------------------数据错误：50001-59999 end

	// -----------------------------------接口错误：60001-69999 start

	// -----------------------------------数据错误：60001-69999 end

	// -----------------------------------权限错误：70001-79999 start
	/**
	 * 无访问权限
	 */
	PERMISSION_NO_ACCESS(70001, "无访问权限"),
	// -----------------------------------接口错误：70001-79999 end

	;

	private Integer code;

	private String message;

	CapitalResponseCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer code() {
		return this.code;
	}

	public String message() {
		return this.message;
	}

	public static String getMessage(String name) {
		for (CapitalResponseCode item : CapitalResponseCode.values()) {
			if (item.name().equals(name)) {
				return item.message;
			}
		}
		return name;
	}

	public static Integer getCode(String name) {
		for (CapitalResponseCode item : CapitalResponseCode.values()) {
			if (item.name().equals(name)) {
				return item.code;
			}
		}
		return null;
	}

}
