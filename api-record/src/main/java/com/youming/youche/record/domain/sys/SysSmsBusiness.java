package com.youming.youche.record.domain.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 短信服务商表
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysSmsBusiness extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 表主键
	 */
	private String bId;

	/**
	 * 业务ID
	 */

	private Long businessId;

	/**
	 * 业务参数
	 */

	private String businessParam;

	/**
	 * 模板ID
	 */

	private Long templateId;

	/**
	 * 发送消息时间
	 */

	private Date businessDate;

	/**
	 * 已处理/未处理：0未处理，1已处理
	 */

	private Integer businessDeal;

	/**
	 * 是要否处理：0不处理，1处理
	 */

	private Integer businessFlag;

	/**
	 * 手机号码
	 */

	private String billId;

	/**
	 * 车队id
	 */
	private Long tenantId;

}
