package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClaimExpenseInfoInDto implements Serializable {

	private Long tenantId;
	private Long expenseId;
	private String  amountString;
	private Long stairCategory;
	private Long secondLevelCategory;
	private String appReason;
	private String accName;
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
	private int accidentType;//事故类型
	private int accidentReason;//出险事故原因
	private int dutyDivide;//责任划分
	private int accidentDivide;//事故司机
	private String accidentExplain;//事故说明
	private String reportNumber;//报案号


	private int expenseType;
	private String specialExpenseNum;
	private int expenseSts;
	private String userName;
	private String userPhone;
	private String startTime;
	private String endTime;
	private String flowId;
	private String orderId;//订单号
	private String bigRootIdIn;
	private String rootOrgIdIn;
	private String orgIdIn;

	private Long weightFee;//过磅重量


	private Integer isNeedBill;//是否需要开票 0无需 1需要
	private int type;//处理类型 1 添加 2修改
	private String imgIds;
	private Integer bankType;//银行卡类型：0、对私，1对公
	private String collectAcctId;//对私收款虚拟账户

	/*微信小程序接口，可以传回多个，逗号分隔*/
	private String stairCategoryList;
	private String secondLevelCategoryList;
	private String	expenseStsList;
	private boolean waitDeal=false;//待我处理
	private String userId;
	private String insuranceEffectiveDate;
	public int getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(int expenseType) {
		this.expenseType = expenseType;
	}

	public String getSpecialExpenseNum() {
		return specialExpenseNum;
	}

	public void setSpecialExpenseNum(String specialExpenseNum) {
		this.specialExpenseNum = specialExpenseNum;
	}

	public Long getStairCategory() {
		return stairCategory;
	}

	public void setStairCategory(Long stairCategory) {
		this.stairCategory = stairCategory;
	}

	public Long getSecondLevelCategory() {
		return secondLevelCategory;
	}

	public void setSecondLevelCategory(Long secondLevelCategory) {
		this.secondLevelCategory = secondLevelCategory;
	}

	public int getExpenseSts() {
		return expenseSts;
	}

	public void setExpenseSts(int expenseSts) {
		this.expenseSts = expenseSts;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getBigRootIdIn() {
		return bigRootIdIn;
	}

	public void setBigRootIdIn(String bigRootIdIn) {
		this.bigRootIdIn = bigRootIdIn;
	}

	public String getRootOrgIdIn() {
		return rootOrgIdIn;
	}

	public void setRootOrgIdIn(String rootOrgIdIn) {
		this.rootOrgIdIn = rootOrgIdIn;
	}

	public String getOrgIdIn() {
		return orgIdIn;
	}

	public void setOrgIdIn(String orgIdIn) {
		this.orgIdIn = orgIdIn;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Long getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Long expenseId) {
		this.expenseId = expenseId;
	}

	public String getAmountString() {
		return amountString;
	}

	public void setAmountString(String amountString) {
		this.amountString = amountString;
	}

	public String getAppReason() {
		return appReason;
	}

	public void setAppReason(String appReason) {
		this.appReason = appReason;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getStrfileId() {
		return strfileId;
	}

	public void setStrfileId(String strfileId) {
		this.strfileId = strfileId;
	}

	public String getCarOwnerName() {
		return carOwnerName;
	}

	public void setCarOwnerName(String carOwnerName) {
		this.carOwnerName = carOwnerName;
	}

	public String getCarPhone() {
		return carPhone;
	}

	public void setCarPhone(String carPhone) {
		this.carPhone = carPhone;
	}

	public Long getAccUserId() {
		return accUserId;
	}

	public void setAccUserId(Long accUserId) {
		this.accUserId = accUserId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAccidentDate() {
		return accidentDate;
	}

	public void setAccidentDate(String accidentDate) {
		this.accidentDate = accidentDate;
	}

	public String getInsuranceDate() {
		return insuranceDate;
	}

	public void setInsuranceDate(String insuranceDate) {
		this.insuranceDate = insuranceDate;
	}

	public String getInsuranceFirm() {
		return insuranceFirm;
	}

	public void setInsuranceFirm(String insuranceFirm) {
		this.insuranceFirm = insuranceFirm;
	}

	public String getInsuranceMoneyString() {
		return insuranceMoneyString;
	}

	public void setInsuranceMoneyString(String insuranceMoneyString) {
		this.insuranceMoneyString = insuranceMoneyString;
	}

	public int getAccidentType() {
		return accidentType;
	}

	public void setAccidentType(int accidentType) {
		this.accidentType = accidentType;
	}

	public int getAccidentReason() {
		return accidentReason;
	}

	public void setAccidentReason(int accidentReason) {
		this.accidentReason = accidentReason;
	}

	public int getDutyDivide() {
		return dutyDivide;
	}

	public void setDutyDivide(int dutyDivide) {
		this.dutyDivide = dutyDivide;
	}

	public int getAccidentDivide() {
		return accidentDivide;
	}

	public void setAccidentDivide(int accidentDivide) {
		this.accidentDivide = accidentDivide;
	}

	public String getAccidentExplain() {
		return accidentExplain;
	}

	public void setAccidentExplain(String accidentExplain) {
		this.accidentExplain = accidentExplain;
	}

	public String getReportNumber() {
		return reportNumber;
	}

	public void setReportNumber(String reportNumber) {
		this.reportNumber = reportNumber;
	}

	public Long getWeightFee() {
		return weightFee;
	}

	public void setWeightFee(Long weightFee) {
		this.weightFee = weightFee;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getImgIds() {
		return imgIds;
	}

	public void setImgIds(String imgIds) {
		this.imgIds = imgIds;
	}

	public String getStairCategoryList() {
		return stairCategoryList;
	}

	public void setStairCategoryList(String stairCategoryList) {
		this.stairCategoryList = stairCategoryList;
	}

	public String getSecondLevelCategoryList() {
		return secondLevelCategoryList;
	}

	public void setSecondLevelCategoryList(String secondLevelCategoryList) {
		this.secondLevelCategoryList = secondLevelCategoryList;
	}

	public String getExpenseStsList() {
		return expenseStsList;
	}

	public void setExpenseStsList(String expenseStsList) {
		this.expenseStsList = expenseStsList;
	}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

	public Integer getIsNeedBill() {
		return isNeedBill;
	}

	public void setIsNeedBill(Integer isNeedBill) {
		this.isNeedBill = isNeedBill;
	}

	public String getInsuranceEffectiveDate() {
		return insuranceEffectiveDate;
	}

	public void setInsuranceEffectiveDate(String insuranceEffectiveDate) {
		this.insuranceEffectiveDate = insuranceEffectiveDate;
	}

	public boolean isWaitDeal() {
		return waitDeal;
	}

	public void setWaitDeal(boolean waitDeal) {
		this.waitDeal = waitDeal;
	}

	public Integer getBankType() {
		return bankType;
	}

	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}

	public String getCollectAcctId() {
		return collectAcctId;
	}

	public void setCollectAcctId(String collectAcctId) {
		this.collectAcctId = collectAcctId;
	}
}
