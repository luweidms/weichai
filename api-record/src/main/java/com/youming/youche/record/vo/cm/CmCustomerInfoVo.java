package com.youming.youche.record.vo.cm;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 客户信息表/客户档案表
 * 入参
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
public class CmCustomerInfoVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 客户编号
	 */
	private Long id;

	/**
	 * 	新增或者修改
	 */
	private Integer verSts;

	/**
	 * 公司名称(全称)
	 */
	private String companyName;
	/**客户状态（ null：全部；1 ：有效 ，2：无效 ）*/
	private String state;
	/**	否	string	认证 （null:全部；1:未认证；2:已认证）*/
	private Integer authState;
	/**客户简称*/
	private String customerName;
	private String customerCode;

	/**
	 * 联系人名称
	 */
	private String lineName;

	/**
	 * 联系电话
	 */
	private String lineTel;

	/**
	 * 联系手机
	 */
	private String linePhone;

	/**
	 * 市场部门
	 */
	private Long saleDaparment;

	/**
	 * 发票抬头
	 */
	private String lookupName;

	/**
	 * 税号
	 */
	private String einNumber;

	/**
	 * 支付方式(对应静态数据的 code_type= PAY_WAY)
	 */
	private Integer payWay;

	private String payWayName;

	/**
	 * KA代表
	 */
	private String maretSale;

	/**
	 * 客户等级(对应静态数据的 code_type= CUSTOMER_LEVEL)
	 */
	private String customerLevel;

	/**
	 * 回单类型(对应静态数据的 code_type=RECIVE_TYPE)
	 */
	private Integer oddWay;

	/**
	 * 回单类型名称
	 */
	private String oddWayName;

	/**
	 * 创建网点ID
	 */
	private Long orgId;

	/**
	 * 创建租户ID
	 */
	private Long tenantId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 操作时间
	 */
	private LocalDateTime updateTime;

	/**
	 * 操作人ID
	 */
	private Long opId;

	/**
	 * 回单期限（天）
	 */
	private Integer reciveTime;

	/**
	 * 对账日（天）
	 */
	private Integer recondTime;

	/**
	 * 开票期限（天）
	 */
	private Integer invoiceTime;

	/**
	 * 收款期限（天）
	 */
	private Integer collectionTime;

	/**
	 * 结算周期天(这个未用（使用收款期限（天） 字段）)
	 */
	private Integer settleCycle;

	/**
	 * 用友编码
	 */
	private String yongyouCode;

	/**
	 * 客户归类
	 */
	private String customerCategory;

	/**
	 * 时效罚款规则(对应静态数据 code_type=LAST_FEE_RULES 默认为4其他客户)
	 */
	private Integer ageFineRule;

	/**
	 * 审核内容
	 */
	private String auditContent;

	/**
	 * 0-不在客户档案展示数据 1-在客户档案展示的数据
	 */
	private Integer type;

	/**
	 * 0否 1是
	 */
	private Integer isAuth;

	/**
	 * 回单地址-省
	 */
	private Long reciveProvinceId;

	private String reciveProvinceName;

	/**
	 * 回单地址-市
	 */
	private Long reciveCityId;

	/**
	 * 回单详细地址
	 */
	private String reciveAddress;

	/**
	 * 对账期限
	 */
	private Integer reconciliationTime;

	/**
	 * 对账期限月
	 */
	private Integer reconciliationMonth;

	/**
	 * 对账期限日
	 */
	private Integer reconciliationDay;

	/**
	 * 回单期限月
	 */
	private Integer reciveMonth;

	/**
	 * 回单期限日
	 */
	private Integer reciveDay;

	/**
	 * 开票期限月
	 */
	private Integer invoiceMonth;

	/**
	 * 开票期限日
	 */
	private Integer invoiceDay;

	/**
	 * 收款期限月
	 */
	private Integer collectionMonth;

	/**
	 * 收款期限日
	 */
	private Integer collectionDay;

	private Integer lineNumber;

	private String address;
	/**失败原因*/
	private String reasonFailure;

}
