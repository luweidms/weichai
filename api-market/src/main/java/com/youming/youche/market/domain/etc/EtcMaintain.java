package com.youming.youche.market.domain.etc;

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
 * ETC表
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class EtcMaintain extends BaseDomain {

	private static final long serialVersionUID = 1L;


	/**
	 * ETC卡号
	 */
	private String etcId;

	/**
	 * 服务商
	 */
	private String serviceName;

	//服务商id
	private Long service_name_id;

	/**
	 * 现绑定的车辆
	 */
	private String bindVehicle;

	/**
	 * 状态 0，无效 1，有效
	 */
	private Integer state;

	/**
	 * 车辆编号
	 */
	private Long vehicleCode;

	/**
	 * 车主ID
	 */
	private Long carUserId;

	/**
	 * 车主
	 */
	private String carOwner;

	/**
	 * 车主手机
	 */
	private String carPhone;

	/**
	 * 供应商账号
	 */
	private Long loginAcct;

	/**
	 * 供应商真实姓名
	 */
	private String linkman;

	/**
	 * 供应商用户ID
	 */
	private Long userId;

	/**
	 * 租户id
	 */
	private Long tenantId;

	/**
	 * ETC卡类型 1粤通卡 2鲁通卡 3赣通卡
	 */
	private Integer etcCardType;

	/**
	 * 付费类型 1预付费 2后付费
	 */
	private Integer paymentType;

	/**
	 * 账期
	 */
	private String accountPeriodRemark;

	/**
	 * 站点名称
	 */
	private String productName;

	/**
	 * 邀请状态 1为邀请
	 */
	private Integer inviteState;

}
