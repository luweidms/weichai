package com.youming.youche.system.domain.tenant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 车队的开票设置
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
@Data
@Accessors(chain = true)
public class BillSetting extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否有开票能力 1、有开票能力，2、没有开票能力
	 */
	private Integer billAbility;

	/**
	 * 平台开票方式
	 */
	private Long billMethod;

	/**
	 * 费率设置
	 */
	private Long rateId;

	/**
	 * 创建日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	/**
	 * 租户ID
	 */
	private Long tenantId;

	/**
	 * 是否支持费非外调车开平台票据（0、不支持；1、支持）
	 */
	private Integer notOtherCarGetPlatformBill;

	private String lugeAcct;

	private String lugePassword;

	private String lugePayPassword;

	/**
	 * 附加运费
	 */
	private Double attachFree;

	@TableField(exist = false)
	private String rateName;

}
