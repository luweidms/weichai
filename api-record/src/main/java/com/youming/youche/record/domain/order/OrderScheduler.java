package com.youming.youche.record.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 订单调度表
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@Table(name = "order_scheduler")
@Entity
@Accessors(chain = true)
public class OrderScheduler implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@TableId(value = "ID", type = IdType.AUTO)
	@Column()
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 订单编号
	 */
	@TableField("ORDER_ID")
	@Column(name = "ORDER_ID")
	private Long orderId;

	/**
	 * 车牌号
	 */
	@TableField("PLATE_NUMBER")
	@Column(name = "PLATE_NUMBER")
	private String plateNumber;

	/**
	 * 司机手机
	 */
	@TableField("CAR_DRIVER_PHONE")
	@Column(name = "CAR_DRIVER_PHONE")
	private String carDriverPhone;

	/**
	 * 司机id
	 */
	@TableField("CAR_DRIVER_ID")
	@Column(name = "CAR_DRIVER_ID")
	private Long carDriverId;

	/**
	 * 司机
	 */
	@TableField("CAR_DRIVER_MAN")
	@Column(name = "CAR_DRIVER_MAN")
	private String carDriverMan;

	/**
	 * 车辆id
	 */
	@TableField("VEHICLE_CODE")
	@Column(name = "VEHICLE_CODE")
	private Long vehicleCode;

	/**
	 * 车长
	 */
	@TableField("CAR_LENGH")
	@Column(name = "CAR_LENGH")
	private String carLengh;

	/**
	 * 车辆种类
	 */
	@TableField("CAR_STATUS")
	@Column(name = "CAR_STATUS")
	private Integer carStatus;

	/**
	 * 用户类型
	 */
	@TableField("CAR_USER_TYPE")
	@Column(name = "CAR_USER_TYPE")
	private Integer carUserType;

	/**
	 * 车辆类型
	 */
	@TableField("VEHICLE_CLASS")
	@Column(name = "VEHICLE_CLASS")
	private Integer vehicleClass;

	/**
	 * 实际靠台时间
	 */
	@TableField("CAR_DEPEND_DATE")
	@Column(name = "CAR_DEPEND_DATE")
	private LocalDateTime carDependDate;

	/**
	 * 实际离台时间
	 */
	@TableField("CAR_START_DATE")
	@Column(name = "CAR_START_DATE")
	private LocalDateTime carStartDate;

	/**
	 * 实际到达时间
	 */
	@TableField("CAR_ARRIVE_DATE")
	@Column(name = "CAR_ARRIVE_DATE")
	private LocalDateTime carArriveDate;

	/**
	 * 调度时间
	 */
	@TableField("DISPATCH_TIME")
	@Column(name = "DISPATCH_TIME")
	private LocalDateTime dispatchTime;

	/**
	 * 副驾驶姓名
	 */
	@TableField("COPILOT_MAN")
	@Column(name = "COPILOT_MAN")
	private String copilotMan;

	/**
	 * 副驾驶手机
	 */
	@TableField("COPILOT_PHONE")
	@Column(name = "COPILOT_PHONE")
	private String copilotPhone;

	/**
	 * 副驾驶id
	 */
	@TableField("COPILOT_USER_ID")
	@Column(name = "COPILOT_USER_ID")
	private Long copilotUserId;

	/**
	 * 校验出发时间
	 */
	@TableField("CHECK_GOING_TIME")
	@Column(name = "CHECK_GOING_TIME")
	private LocalDateTime checkGoingTime;

	/**
	 * 校验到达时间
	 */
	@TableField("CHECK_ARRIVE_TIME")
	@Column(name = "CHECK_ARRIVE_TIME")
	private LocalDateTime checkArriveTime;

	/**
	 * 校验时间
	 */
	@TableField("CHECK_TIME")
	@Column(name = "CHECK_TIME")
	private LocalDateTime checkTime;

	/**
	 * 调度员名字
	 */
	@TableField("DISPATCHER_NAME")
	@Column(name = "DISPATCHER_NAME")
	private String dispatcherName;

	/**
	 * 调度员ID
	 */
	@TableField("DISPATCHER_ID")
	@Column(name = "DISPATCHER_ID")
	private Long dispatcherId;

	/**
	 * 离场时间
	 */
	@TableField("CAR_DEPARTURE_DATE")
	@Column(name = "CAR_DEPARTURE_DATE")
	private LocalDateTime carDepartureDate;

	/**
	 * 挂车ID
	 */
	@TableField("TRAILER_ID")
	@Column(name = "TRAILER_ID")
	private Long trailerId;

	/**
	 * 挂车车牌
	 */
	@TableField("TRAILER_PLATE")
	@Column(name = "TRAILER_PLATE")
	private String trailerPlate;

	/**
	 * 车辆类型(整车/拖头)
	 */
	@TableField("LICENCE_TYPE")
	@Column(name = "LICENCE_TYPE")
	private Integer licenceType;

	/**
	 * 会员等级
	 */
	@TableField("CREDIT_LEVEL")
	@Column(name = "CREDIT_LEVEL")
	private Integer creditLevel;

	/**
	 * 运输距离
	 */
	@TableField("DISTANCE")
	@Column(name = "DISTANCE")
	private Long distance;

	/**
	 * 派单方式（1 竞价 2 抢单 3 指派）
	 */
	@TableField("APPOINT_WAY")
	@Column(name = "APPOINT_WAY")
	private Integer appointWay;

	/**
	 * 接收范围（1 全部 2 铜牌会员 3 银牌会员 4 金牌会员）
	 */
	@TableField("RECEIVE_USER")
	@Column(name = "RECEIVE_USER")
	private String receiveUser;

	/**
	 * 抢单轮次（目前总共为三次）
	 */
	@TableField("APPOINT_COUNT")
	@Column(name = "APPOINT_COUNT")
	private Integer appointCount;

	/**
	 * 定标时间
	 */
	@TableField("SCALING_TIME")
	@Column(name = "SCALING_TIME")
	private LocalDateTime scalingTime;

	/**
	 * 是否为紧急加班车（1 是的 0 不是 ）
	 */
	@TableField("IS_URGENT")
	@Column(name = "IS_URGENT")
	private Integer isUrgent;

	/**
	 * 创建时间
	 */
	@TableField("CREATE_DATE")
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;

	/**
	 * 修改时间
	 */
	@TableField("UPDATE_DATE")
	@Column(name = "UPDATE_DATE")
	private LocalDateTime updateDate;

	/**
	 * 操作员ID
	 */
	@TableField("OP_ID")
	@Column(name = "OP_ID")
	private Long opId;

	/**
	 * 租户id
	 */
	@TableField("TENANT_ID")
	@Column(name = "TENANT_ID")
	private Long tenantId;

	/**
	 * 线路ID
	 */
	@TableField("SOURCE_ID")
	@Column(name = "SOURCE_ID")
	private Long sourceId;

	/**
	 * 修改数据的操作人id
	 */
	@TableField("UPDATE_OP_ID")
	@Column(name = "UPDATE_OP_ID")
	private Long updateOpId;

	/**
	 * 调度员手机
	 */
	@TableField("DISPATCHER_BILL")
	@Column(name = "DISPATCHER_BILL")
	private String dispatcherBill;

	/**
	 * 靠台时间
	 */
	@TableField("DEPEND_TIME")
	@Column(name = "DEPEND_TIME")
	private Date dependTime;

	/**
	 * 到达时限
	 */
	@TableField("ARRIVE_TIME")
	@Column(name = "ARRIVE_TIME")
	private Float arriveTime;

	/**
	 * 线路合同ID
	 */
	@TableField("CLIENT_CONTRACT_ID")
	@Column(name = "CLIENT_CONTRACT_ID")
	private Long clientContractId;

	/**
	 * 线路合同URL
	 */
	@TableField("CLIENT_CONTRACT_URL")
	@Column(name = "CLIENT_CONTRACT_URL")
	private String clientContractUrl;

	/**
	 * 线路编码
	 */
	@TableField("SOURCE_CODE")
	@Column(name = "SOURCE_CODE")
	private String sourceCode;

	/**
	 * 回单地址
	 */
	@TableField("RECIVE_ADDR")
	@Column(name = "RECIVE_ADDR")
	private String reciveAddr;

	/**
	 * 代收人手机
	 */
	@TableField("COLLECTION_USER_PHONE")
	@Column(name = "COLLECTION_USER_PHONE")
	private String collectionUserPhone;

	/**
	 * 代收人名称
	 */
	@TableField("COLLECTION_USER_NAME")
	@Column(name = "COLLECTION_USER_NAME")
	private String collectionUserName;

	/**
	 * 代收人ID
	 */
	@TableField("COLLECTION_USER_ID")
	@Column(name = "COLLECTION_USER_ID")
	private Long collectionUserId;

	/**
	 * 是否代收 0 不代收 1 代收
	 */
	@TableField("IS_COLLECTION")
	@Column(name = "IS_COLLECTION")
	private Integer isCollection;

	/**
	 * 供应商公里数
	 */
	@TableField("MILEAGE_NUMBER")
	@Column(name = "MILEAGE_NUMBER")
	private Integer mileageNumber;

	/**
	 * 值班司机ID
	 */
	@TableField("ON_DUTY_DRIVER_ID")
	@Column(name = "ON_DUTY_DRIVER_ID")
	private Long onDutyDriverId;

	/**
	 * 值班司机名称
	 */
	@TableField("ON_DUTY_DRIVER_NAME")
	@Column(name = "ON_DUTY_DRIVER_NAME")
	private String onDutyDriverName;

	/**
	 * 值班司机手机
	 */
	@TableField("ON_DUTY_DRIVER_PHONE")
	@Column(name = "ON_DUTY_DRIVER_PHONE")
	private String onDutyDriverPhone;

	/**
	 * 线路名称
	 */
	@TableField("SOURCE_NAME")
	@Column(name = "SOURCE_NAME")
	private String sourceName;

	/**
	 * 校验靠台时间
	 */
	@TableField("VERIFY_DEPEND_DATE")
	@Column(name = "VERIFY_DEPEND_DATE")
	private LocalDateTime verifyDependDate;

	/**
	 * 校验离台时间
	 */
	@TableField("VERIFY_START_DATE")
	@Column(name = "VERIFY_START_DATE")
	private LocalDateTime verifyStartDate;

	/**
	 * 校验到达时间
	 */
	@TableField("VERIFY_ARRIVE_DATE")
	@Column(name = "VERIFY_ARRIVE_DATE")
	private LocalDateTime verifyArriveDate;

	/**
	 * 校验离场时间
	 */
	@TableField("VERIFY_DEPARTURE_DATE")
	@Column(name = "VERIFY_DEPARTURE_DATE")
	private LocalDateTime verifyDepartureDate;

	/**
	 * 校验靠台附件ID
	 */
	@TableField("VERIFY_DEPEND_FILE_ID")
	@Column(name = "VERIFY_DEPEND_FILE_ID")
	private Long verifyDependFileId;

	/**
	 * 校验靠台附件URL
	 */
	@TableField("VERIFY_DEPEND_FILE_URL")
	@Column(name = "VERIFY_DEPEND_FILE_URL")
	private String verifyDependFileUrl;

	/**
	 * 校验离台附件ID
	 */
	@TableField("VERIFY_START_FILE_ID")
	@Column(name = "VERIFY_START_FILE_ID")
	private Long verifyStartFileId;

	/**
	 * 校验离台附件URL
	 */
	@TableField("VERIFY_START_FILE_URL")
	@Column(name = "VERIFY_START_FILE_URL")
	private String verifyStartFileUrl;

	/**
	 * 校验到达附件ID
	 */
	@TableField("VERIFY_ARRIVE_FILE_ID")
	@Column(name = "VERIFY_ARRIVE_FILE_ID")
	private Long verifyArriveFileId;

	/**
	 * 校验到达附件URL
	 */
	@TableField("VERIFY_ARRIVE_FILE_URL")
	@Column(name = "VERIFY_ARRIVE_FILE_URL")
	private String verifyArriveFileUrl;

	/**
	 * 校验离场附件ID
	 */
	@TableField("VERIFY_DEPARTURE_FILE_ID")
	@Column(name = "VERIFY_DEPARTURE_FILE_ID")
	private Long verifyDepartureFileId;

	/**
	 * 校验离场附件URL
	 */
	@TableField("VERIFY_DEPARTURE_FILE_URL")
	@Column(name = "VERIFY_DEPARTURE_FILE_URL")
	private String verifyDepartureFileUrl;

	/**
	 * 账单接收人用户编号
	 */
	@TableField("BILL_RECEIVER_USER_ID")
	@Column(name = "BILL_RECEIVER_USER_ID")
	private Long billReceiverUserId;

	/**
	 * 账单接收人姓名
	 */
	@TableField("BILL_RECEIVER_NAME")
	@Column(name = "BILL_RECEIVER_NAME")
	private String billReceiverName;

	/**
	 * 账单接收人手机号
	 */
	@TableField("BILL_RECEIVER_MOBILE")
	@Column(name = "BILL_RECEIVER_MOBILE")
	private String billReceiverMobile;

}
