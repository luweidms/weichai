package com.youming.youche.record.domain.tenant;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 租户会员关系
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantUserRelVer extends BaseDomain {

	private static final long serialVersionUID = 1L;



	/**
	 * 关系ID
	 */

	private Long relId;

	/**
	 * 用户编号
	 */
	private Long userId;

	/**
	 * 车队id
	 */
	private Long tenantId;

	/**
	 * 是否认证1、未认证 2、认证中 3、未通过 5、已认证
	 */

	private Integer state;

	/**
	 * 二级组织编号
	 */

	private Long orgId;

	/**
	 * 审核原因
	 */

	private String stateReason;

	/**
	 * 管理人员备注
	 */

	private String managerRemark;

	/**
	 * 操作人ID
	 */

	private Long opId;


	/**
	 * 授权通过日期
	 */
	private LocalDateTime authPassDate;

	/**
	 * 审核紧急程度1:非常紧急2:普通
	 */

	private Integer authImportant;

	/**
	 * 紧急原因:可标注资料补充上传二次审核等原因
	 */

	private String authImportantReason;

	/**
	 * 审核人
	 */

	private Long authManId;

	/**
	 * 操作人
	 */
	private Long authOrgId;

	/**
	 * 业务帐单编号
	 */
	private String businessBillId;

	/**
	 * 车主（司机）类型（DRIVER_TYPE）
	 */

	private Integer carUserType;

	/**
	 * 路歌返回信息
	 */

	private String retResult;

	/**
	 * 路歌调用时间
	 */

	private LocalDateTime retDate;

	/**
	 * 路歌返回不通过原因:根据数字位数标识含义，每位数字含义参见
	 */

	private String retDetail;

	/**
	 * 审核状态 0：未审核 1:已审核(枚举HAS_VER_STATE)
	 */

	private Integer hasVer;

	/**
	 * （是否审核通过：0为未通过，1为通过）
	 */

	private Integer userVerifStatu;

	/**
	 * 审核意见
	 */

	private String verifReason;

	/**
	 * 上一单单号
	 */

	private Long beforeOrder;

	/**
	 * 当前单号
	 */

	private Long currOrder;

	/**
	 * 来源租户，用于自有司机迁移
	 */

	private Long sourceTenantId;


	/**
	 * 修改操作人
	 */

	private Long updateOpId;

	/**
	 * 审核数据状态
	 */

	private Integer verState;

	/**
	 * 归属部门
	 */

	private Long attachedOrgId;

	/**
	 * 归属人
	 */
	private Long attachedUserId;

}
