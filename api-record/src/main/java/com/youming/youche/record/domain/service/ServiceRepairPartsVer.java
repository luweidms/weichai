package com.youming.youche.record.domain.service;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**

 * 维修零配件版本

 */
@Data
@Accessors(chain = true)
public class ServiceRepairPartsVer implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * 主键
	 */
	@TableId(
			value = "id",
			type = IdType.AUTO
	)
	private Long hisId;

	@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@TableField(
			value = "create_time",
			fill = FieldFill.INSERT
	)
	private LocalDateTime createTime;
	@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@TableField(
			value = "update_time",
			fill = FieldFill.INSERT_UPDATE
	)
	private LocalDateTime updateTime;

	/**
	 * 主表id
	 */
	private Long id;

	/**
	 * 配件id
	 */
	private Long partsId;

	/**
	 * 项目id
	 */
	private Long orderItemId;

	/**
	 * 维修保养订单id
	 */
	private Long repairOrderId;

	/**
	 * 配件名称
	 */
	private String partsName;

	/**
	 * 配件数量
	 */
	private Double partsNumber;

	/**
	 * 配件单价
	 */
	private Long partsPrice;

	/**
	 * 配件总价
	 */
	private Long totalPartsPrice;

	/**
	 * 车队id
	 */
	private Long tenantId;

	/**
	 * 维修保养主历史表id
	 */
	private Long repairHisId;

}
