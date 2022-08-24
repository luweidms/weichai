package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.ClaimExpenseInfo;
import com.youming.youche.finance.domain.OaFiles;
import com.youming.youche.finance.dto.ClaimExpenseCategoryDto;
import com.youming.youche.finance.dto.ClaimExpenseInfoByIdDto;
import com.youming.youche.finance.dto.ClaimExpenseInfoDto;
import com.youming.youche.finance.dto.ClaimExpenseInfoInDto;
import com.youming.youche.finance.dto.QueryClaimExpenseInfoDto;
import com.youming.youche.finance.dto.UserDataInfoDto;
import com.youming.youche.finance.dto.VehicleListDto;
import com.youming.youche.finance.vo.ClaimExpenseInfoVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车管报销表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
public interface IClaimExpenseInfoService extends IBaseService<ClaimExpenseInfo> {

	/**
	 * 方法实现说明 创建费用管理申请
	 * @author terry
	 * @param infoIn * @param userDataInfoDto 收款用户信息 * @param amount 申请金额 * @param
	 * insuranceMoney 理赔金额 * @param accessToken * @param b
	 * @return long 返回id 失败返回-1
	 * @exception
	 * @date 2022/1/27 23:36
	 */
	long create(ClaimExpenseInfoVo infoIn, UserDataInfoDto userDataInfoDto, Long amount, Long insuranceMoney,
			String accessToken, boolean b);

	// 修改费用管理申请
	Long update(ClaimExpenseInfoVo infoIn, UserDataInfoDto userDataInfoDto, Long amount, Long insuranceMoney,
			String accessToken, boolean b);


	////查詢核銷圖片
	List<OaFiles> queryOaFilesByRelType4(Long id, Long tenantId);

	/**
	 * 管理费用列表（车管 和司机）
	 * @param expenseType
	 * @param specialExpenseNum
	 * @param stairCategory
	 * @param secondLevelCategory
	 * @param expenseSts
	 * @param userName
	 * @param userPhone
	 * @param plateNumber
	 * @param startTime
	 * @param endTime
	 * @param flowId
	 * @param orderId
	 * @param oneOrgId
	 * @param twoOrgId
	 * @param threeOrgId
	 * @param stairCategoryList
	 * @param secondLevelCategoryList
	 * @param waitDeal
	 * @param dataPermissionIds
	 * @param subOrgList
	 * @param pageNum
	 * @param pageSize
	 * @param accessToken
	 * @return
	 */
	IPage<ClaimExpenseCategoryDto> doQuery(Integer expenseType, String specialExpenseNum,
										   String stairCategory, String secondLevelCategory,
										   Integer expenseSts, String userName,
										   String userPhone, String plateNumber, String startTime,
										   String endTime, String flowId, String orderId,
										   String oneOrgId, String twoOrgId, String threeOrgId,
										   List<String> stairCategoryList, List<String> secondLevelCategoryList,
										   Boolean waitDeal, List<Long> dataPermissionIds,
										   Integer pageNum, Integer pageSize, String accessToken,
										   List<String> expenseStsList,String userId);

	/**
	 * 查询报销详情
	 * @param expenseId
	 * @param busiCode 业务编码
	 * @return
	 */
	ClaimExpenseInfo queryClaimExpenseById(Long expenseId, String accessToken, String ...busiCode);

	/**
	 * ID查询
	 * @param expenseId
	 * @return
	 */
	ClaimExpenseInfo getClaimExpenseInfo(Long expenseId, String ...busiCode);


	/**
	 * 取消报销申请
	 * expenseId 主键
	 * @return
	 */
	void cancelClaimExpense(Long expenseId ,String accessToken);

	/**
	 * 审核报销信息
	 * 审核后页面回调：修改报销状态和银行信息
	 */
	void examineClaimExpense(ClaimExpenseInfoVo infoIn,String accessToken);


	// 导出费用管理
	void  excelExport(Integer expenseType, String specialExpenseNum,
					  String stairCategory, String secondLevelCategory,
					  Integer expenseSts, String userName,
					  String userPhone, String plateNumber, String startTime,
					  String endTime, String flowId, String orderId,
					  String oneOrgId, String twoOrgId, String threeOrgId,
					  List<String> stairCategoryList, List<String> secondLevelCategoryList,
					  Boolean waitDeal, List<Long> dataPermissionIds,
					  Integer pageNum, Integer pageSize, String accessToken,
					  List<String> expenseStsList, String userId, ImportOrExportRecords record );

	/**
	 * 营运工作台  管理费用  待我审
	 */
	List<WorkbenchDto> getTableManageCostCount();

	/**
	 * 营运工作台  管理费用  我发起
	 */
	List<WorkbenchDto> getTableManageCostMeCount();

	/**
	 * 流程结束，审核通过的回调方法
	 * @param busiId    业务的主键
	 * @param desc      结果的描述
	 * @return
	 */
	void sucess(Long busiId, String desc, Map paramsMap, String accessToken);

	/**
	 * 流程结束，审核不通过的回调方法
	 * @param busiId    业务的主键
	 * @param desc      结果的描述
	 * @return
	 */
	void fail(Long busiId, String desc, Map paramsMap, String accessToken);

	/**
	 * 该函数的功能描述:查询报销数量
	 */
	Integer queryClaimInfoCount(String accessToken);

	/**
	 * 报销审核-报销列表查询
	 * 接口编码：21110
	 */
	Page<ClaimExpenseInfoDto> doQueryWx(ClaimExpenseInfoVo vo, String accessToken, Integer pageNum, Integer pageSize);

	/**
	 * 报销审核-报销详情
	 * 接口编码：21111
	 */
	ClaimExpenseInfoByIdDto getClaimExpenseInfoById(Long expenseId, String accessToken);

	/**
	 * 报销审核-司机报销审核
	 * 接口编码：21112
	 *
	 * @param expenseId
	 * @param desc
	 * @param chooseResult 1 审核通过，2 审核不通过
	 */
	void driverExpenseAudit(Long expenseId, String desc, Integer chooseResult, String accessToken);

	/**
	 * 报销审核-车管报销审核
	 * 接口编码：21113
	 *
	 * @param expenseId
	 * @param desc
	 * @param chooseResult
	 */
	void tubeExpenseAudit(Long expenseId, String desc, Integer chooseResult, String accessToken);

	/**
	 * 我的订单-报销-报销列表(21100)
	 * @param orderId
	 * @param accessToken
	 * @return
	 */
	List<QueryClaimExpenseInfoDto> queryClaimExpenseInfoByOrderId(Long orderId, String accessToken);

	/**
	 * 	/**
	 * 	 * 报销审核-报销详情
	 * 	 * 接口编码：21101
	 *
	 */
	QueryClaimExpenseInfoDto getClaimExpenseInfoByIds(Long expenseId, String accessToken);

	String saveOrUpdateExpenseDriver(ClaimExpenseInfoInDto infoIn,String accessToken);
	/**
	 * 取消报销申请
	 * expenseId 主键
	 * @return
	 */
	String cancelClaimExpenses(Long expenseId, String accessToken);


	/**
	 *
	 * @param nodeIndex  当前节点是第几个节点
	 * @param busiId 业务主键id
	 * @param targetObjectType   com.business.consts.AuditConsts.TargetObjType
	 * 							角色类型
	public static final int ROLE_TYPE = 0;
	组织类型
	public static final int  ORG_TYPE= 1;
	用户类型
	public static final int  USER_TYPE= 2;
	 * @param targetObjId    对应的值，用逗号隔开
	 * @throws Exception
	 */
	public void notice(String accessToken,Integer nodeIndex,Long busiId, Integer targetObjectType, String targetObjId) throws Exception;

}
