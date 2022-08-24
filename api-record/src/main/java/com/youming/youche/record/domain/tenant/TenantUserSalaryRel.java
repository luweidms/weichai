package com.youming.youche.record.domain.tenant;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 租户自有司机收入信息
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantUserSalaryRel extends BaseDomain {

	private static final long serialVersionUID = 1L;




	/**
	 * 用户编号
	 */

	private Long userId;

	/**
	 * 车队id
	 */
	private Long tenantId;

	/**
	 * 租户会员关系ID
	 */
	private Long relId;

	/**
	 * 是否认证1、未认证 2、认证中 3、未通过 5、已认证
	 */

	private Integer state;

	/**
	 * 油老板收入帐期（单位天）
	 */

	private Integer accountPeriod;

	/**
	 * 薪资
	 */

	private Long salary;

	/**
	 * 出车现金
	 */

	private Long dispatchCash;

	/**
	 * 补贴
	 */

	private Long subsidy;

	/**
	 * 维修补贴
	 */

	private Long repairSubsidy;

	/**
	 * 结算日
	 */

	private Integer accDay;

	/**
	 * 在职状态
	 */

	private Integer staffState;

	/**
	 * 入职时间
	 */

	private LocalDateTime entryDate;

	/**
	 * 归属省份
	 */

	private Long attachedProvince;

	/**
	 * 归属城市
	 */

	private Long attachedRegion;

	/**
	 * 归属大区ID
	 */

	private Long attachedRootOrgId;

	/**
	 * 归属运营部门
	 */

	private Long attachedRootOrgTwoId;

	/**
	 * 工资模式(1:普通,2:里程模式)
	 */

	private Integer salaryPattern;

	/**
	 * 操作员id
	 */
	private Long opId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createDate;

}
