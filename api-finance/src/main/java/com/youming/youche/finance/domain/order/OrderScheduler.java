package com.youming.youche.finance.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 订单调度表
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderScheduler extends BaseDomain {

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 订单编号
	 */
	private Long orderId;

	/**
	 * 车牌号
	 */
	private String plateNumber;

	/**
	 * 司机手机
	 */
	private String carDriverPhone;

	/**
	 * 司机id
	 */
	private Long carDriverId;

	/**
	 * 司机
	 */
	private String carDriverMan;

	/**
	 * 车辆id
	 */
	private Long vehicleCode;

	/**
	 * 车长
	 */
	private String carLengh;

	/**
	 * 车辆种类
	 */
	private Integer carStatus;

	/**
	 * 用户类型
	 */
	private Integer carUserType;

	/**
	 * 车辆类型
	 */
	private Integer vehicleClass;

	/**
	 * 实际靠台时间
	 */
	private LocalDateTime carDependDate;

	/**
	 * 实际离台时间
	 */
	private LocalDateTime carStartDate;

	/**
	 * 实际到达时间
	 */
	private LocalDateTime carArriveDate;

	/**
	 * 调度时间
	 */
	private LocalDateTime dispatchTime;

	/**
	 * 副驾驶姓名
	 */
	private String copilotMan;

	/**
	 * 副驾驶手机
	 */
	private String copilotPhone;

	/**
	 * 副驾驶id
	 */
	private Long copilotUserId;

	/**
	 * 校验出发时间
	 */
	private LocalDateTime checkGoingTime;

	/**
	 * 校验到达时间
	 */
	private LocalDateTime checkArriveTime;

	/**
	 * 校验时间
	 */
	private LocalDateTime checkTime;

	/**
	 * 调度员名字
	 */
	private String dispatcherName;

	/**
	 * 调度员ID
	 */
	private Long dispatcherId;

	/**
	 * 离场时间
	 */
	private LocalDateTime carDepartureDate;

	/**
	 * 挂车ID
	 */
	private Long trailerId;

	/**
	 * 挂车车牌
	 */
	private String trailerPlate;

	/**
	 * 车辆类型(整车/拖头)
	 */
	private Integer licenceType;

	/**
	 * 会员等级
	 */
	private Integer creditLevel;

	/**
	 * 运输距离
	 */
	private Long distance;

	/**
	 * 派单方式（1 竞价 2 抢单 3 指派）
	 */
	private Integer appointWay;

	/**
	 * 接收范围（1 全部 2 铜牌会员 3 银牌会员 4 金牌会员）
	 */
	private String receiveUser;

	/**
	 * 抢单轮次（目前总共为三次）
	 */
	private Integer appointCount;

	/**
	 * 定标时间
	 */
	private LocalDateTime scalingTime;

	/**
	 * 是否为紧急加班车（1 是的 0 不是 ）
	 */
	private Integer isUrgent;

	/**
	 * 操作员ID
	 */
	private Long opId;

	/**
	 * 租户id
	 */
	private Long tenantId;

	/**
	 * 线路ID
	 */
	private Long sourceId;

	/**
	 * 修改数据的操作人id
	 */
	private Long updateOpId;

	/**
	 * 调度员手机
	 */
	private String dispatcherBill;

	/**
	 * 靠台时间
	 */
	private LocalDateTime dependTime;

	/**
	 * 到达时限
	 */
	private Float arriveTime;

	/**
	 * 线路合同ID
	 */
	private Long clientContractId;

	/**
	 * 线路合同URL
	 */
	private String clientContractUrl;

	/**
	 * 线路编码
	 */
	private String sourceCode;

	/**
	 * 回单地址
	 */
	private String reciveAddr;

	/**
	 * 代收人手机
	 */
	private String collectionUserPhone;

	/**
	 * 代收人名称
	 */
	private String collectionUserName;

	/**
	 * 代收人ID
	 */
	private Long collectionUserId;

	/**
	 * 是否代收 0 不代收 1 代收
	 */
	private Integer isCollection;

	/**
	 * 供应商公里数
	 */
	private Integer mileageNumber;

	/**
	 * 值班司机ID
	 */
	private Long onDutyDriverId;

	/**
	 * 值班司机名称
	 */
	private String onDutyDriverName;

	/**
	 * 值班司机手机
	 */
	private String onDutyDriverPhone;

	/**
	 * 线路名称
	 */
	private String sourceName;

	/**
	 * 校验靠台时间
	 */
	private LocalDateTime verifyDependDate;

	/**
	 * 校验离台时间
	 */
	private LocalDateTime verifyStartDate;

	/**
	 * 校验到达时间
	 */
	private LocalDateTime verifyArriveDate;

	/**
	 * 校验离场时间
	 */
	private LocalDateTime verifyDepartureDate;

	/**
	 * 校验靠台附件ID
	 */
	private Long verifyDependFileId;

	/**
	 * 校验靠台附件URL
	 */
	private String verifyDependFileUrl;

	/**
	 * 校验离台附件ID
	 */
	private Long verifyStartFileId;

	/**
	 * 校验离台附件URL
	 */
	private String verifyStartFileUrl;

	/**
	 * 校验到达附件ID
	 */
	private Long verifyArriveFileId;

	/**
	 * 校验到达附件URL
	 */
	private String verifyArriveFileUrl;

	/**
	 * 校验离场附件ID
	 */
	private Long verifyDepartureFileId;

	/**
	 * 校验离场附件URL
	 */
	private String verifyDepartureFileUrl;

	/**
	 * 账单接收人用户编号
	 */
	private Long billReceiverUserId;

	/**
	 * 账单接收人姓名
	 */
	private String billReceiverName;

	/**
	 * 账单接收人手机号
	 */
	private String billReceiverMobile;

}
