package com.youming.youche.record.domain.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 短信日志表
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysSmsLog extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private Double smsLogId;

	/**
	 * 短信表ID
	 */
	private Long smsId;

	/**
	 * 手机号码
	 */
	private String billId;

	/**
	 * 短信模板ID
	 */
	private Long templateId;

	/**
	 * 短信内容
	 */
	private String smsContent;

	/**
	 * 发现日期
	 */
	private Date sendData;

	/**
	 * 车队ID
	 */
	private Long tenantId;

}
