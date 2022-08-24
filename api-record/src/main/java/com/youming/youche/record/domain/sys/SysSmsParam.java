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

/**
 * <p>
 * 短信参数表
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysSmsParam extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private Long paramId;

	/**
	 * 模板Id
	 */
	private Long templateId;

	/**
	 * 参数名称
	 */
	private String paramName;

	/**
	 * 参数code
	 */
	private String paramCode;

	/**
	 * 参数值表达式
	 */
	private String paramValueExpr;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * '0:无效 1:有效';
	 */
	private Integer state;

	/**
	 * 租户Id
	 */
	private Long tenantId;

}
