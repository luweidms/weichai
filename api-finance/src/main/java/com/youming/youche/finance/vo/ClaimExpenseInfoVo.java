package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClaimExpenseInfoVo implements Serializable {

	private static final long serialVersionUID = -9148277219374238623L;

	private Integer isNeedBill;// 是否需要开票 0无需 1需要

	private int type;// 处理类型 1 添加 2修改

	private String imgIds; //图片id

	private Integer bankType;// 银行卡类型：0、对私，1对公

	private String collectAcctId;// 对私收款虚拟账户

	private Long tenantId; //租户id

	private Integer state; //报销状态

	private Long expenseId; //主键

	private Long stairCategory; //一级类目

	private Long secondLevelCategory; //二级类目

	private String appReason; //借款原因

	private String accName; //帐户名

	private String accNo; //账号

	private String bankName; //开户行

	private String bankBranch; //银行支行

	private String strfileId; //文件id

	private String plateNumber;// 车牌号

	private String carOwnerName;// 司机名字

	private String carPhone;// 手机

	private Long accUserId;// 申请人id

	private String remark;// 说明

	private String userName;// "照顾"

	private String orgName;// "招商挂靠333"

	private Long orgId; //部门id

	/** 申请金额 */
	private String amount;// "1233"

	private String borrowName;// "招商司机51"

	private String borrowPhone;// "13699993051"

//	private Long fileId;// "10757250771000,"

	private String accidentDate;// 事故时间

	private String insuranceDate;// 出险时间

	private String insuranceFirm;// 保险公司

	private String insuranceMoneyString;// 理赔金额

	private int accidentType;// 事故类型

	private int accidentReason;// 出险事故原因

	private int dutyDivide;// 责任划分

	private int accidentDivide;// 事故司机

	private String accidentExplain;// 事故说明

	private String reportNumber;// 报案号

	private Integer expenseType; //报销类型

	private String specialExpenseNum; //报销编号

	private int expenseSts; //报销状态

	// private String userName;
	private String userPhone; //用户手机号

	private String startTime; //开始时间

	private String endTime; //结束时间

	private String flowId; //支付id

	private String orderId;// 订单号

	private String bigRootIdIn; //

	private String rootOrgIdIn; //

	private String orgIdIn; //

	private Long weightFee;// 过磅重量

	// /*微信小程序接口，可以传回多个，逗号分隔*/
	private String stairCategoryList; //一级科目名称
	private String secondLevelCategoryList; //二级科目名称
	private String expenseStsList; //费用状态类型
	private Boolean waitDeal; // 待我处理
	private String userId; //用户ID
	private String insuranceEffectiveDate; //保险生效时间

	/**
	 * 车辆id
	 */
	private  Long vehicleCode;
	/**
	 * 车辆类型
	 */
	private  Integer vehicleClass;
}
