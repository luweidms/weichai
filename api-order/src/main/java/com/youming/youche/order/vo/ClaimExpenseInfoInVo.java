package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClaimExpenseInfoInVo implements Serializable {

	private Long tenantId;
	private Long expenseId;
	private String amountString;
	private Long stairCategory;
	private Long secondLevelCategory;
	private String appReason;
	private String accName;
	/**
	 * 账号
	 */
	private String accNo;
	private String bankName;
	private String bankBranch;
	private String strfileId;
	private String plateNumber;//车牌号
	private String carOwnerName;//司机名字
	private String carPhone;//手机
	private Long accUserId;//申请人id
	private String remark;// 说明

	private String accidentDate;//事故时间
	private String insuranceDate;//出险时间
	private String insuranceFirm;//保险公司
	private String insuranceMoneyString;//理赔金额
	private Integer accidentType;//事故类型
	private Integer accidentReason;//出险事故原因
	private Integer dutyDivide;//责任划分
	private Integer accidentDivide;//事故司机
	private String accidentExplain;//事故说明
	private String reportNumber;//报案号


	private Integer expenseType;
	private String specialExpenseNum;
	private Integer expenseSts;
	private String userName;
	private String userPhone;
	private String startTime;
	private String endTime;
	/**
	 * 流水号
	 */
	private String flowId;
	private String orderId;//订单号
	private String bigRootIdIn;
	private String rootOrgIdIn;
	private String orgIdIn;

	private Long weightFee;//过磅重量


	private Integer isNeedBill;//是否需要开票 0无需 1需要
	private Integer type;//处理类型 1 添加 2修改
	private String imgIds;
	private Integer bankType;//银行卡类型：0、对私，1对公
	private String collectAcctId;//对私收款虚拟账户

	/*微信小程序接口，可以传回多个，逗号分隔*/
	private String stairCategoryList;
	private String secondLevelCategoryList;
	private String expenseStsList;
	private Boolean waitDeal = false;//待我处理
	private String userId;//订单用户Id
	private String insuranceEffectiveDate;
	private String queryMonth;
	private List<Long> orgIds;
}
