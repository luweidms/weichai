package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单货物表
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderGoods extends BaseDomain {

	/**
	 * 主键
	 */
	private Long Id;

	/**
	 * 起始地
	 */
	private String source;

	/**
	 * 目的地
	 */
	private String des;

	/**
	 * 订单编号
	 */
	private Long orderId;

	/**
	 * 货物名称
	 */
	private String goodsName;

	/**
	 * 货物体积
	 */
	private Float square;

	/**
	 * 货物件数
	 */
	private Integer nums;

	/**
	 * 货物重量KG
	 */
	private Float weight;

	/**
	 * 来源租户id
	 */
	private Long fromTenantId;

	/**
	 * 客户名称
	 */
	private String customName;

	/**
	 * 客户id
	 */
	private Long customUserId;

	/**
	 * 收货人
	 */
	private String reciveName;

	/**
	 * 收货电话
	 */
	private String recivePhone;

	/**
	 * 详细地址
	 */
	private String addrDtl;

	/**
	 * 联系电话
	 */
	private String contactPhone;

	/**
	 * 省份ID
	 */
	private Integer provinceId;

	/**
	 * 市编码ID
	 */
	private Integer cityId;

	/**
	 * 县区ID
	 */
	private Integer countyId;

	/**
	 * 地址纬度
	 */
	private String nand;

	/**
	 * 地址经度
	 */
	private String eand;

	/**
	 * 客户单号（跟cutom_id的关系）
	 */
	private String customOrderId;

	/**
	 * 目的纬度
	 */
	private String nandDes;

	/**
	 * 目的经度
	 */
	private String eandDes;

	/**
	 * 需求车长
	 */
	private String vehicleLengh;

	/**
	 * 需求车辆类型
	 */
	private Integer vehicleStatus;

	/**
	 * 驻场调度人员 id
	 */
	private Long localUser;

	/**
	 * 驻场调度手机
	 */
	private String localPhone;

	/**
	 * 目的地详细地址
	 */
	private String desDtl;

	/**
	 * 回单类型
	 */
	private Integer reciveType;

	/**
	 * 回单状态
	 */
	private Integer reciveState;

	/**
	 * 驻场人名
	 */
	private String localUserName;

	/**
	 * 文字描述目的地址，不填写默认使用百度定位地址,做为百度地址的备注
	 */
	private String navigatDesLocation;

	/**
	 * 文字描述出发地址，不填写默认使用百度定位地址,做为百度地址的备注
	 */
	private String navigatSourceLocation;

	/**
	 * 合同状态
	 */
	private Integer loadState;

	/**
	 * 操作员ID
	 */
	private Long opId;

	/**
	 * 租户id
	 */
	private Long tenantId;

	/**
	 * 修改数据的操作人id
	 */
	private Long updateOpId;

	/**
	 * 联系人
	 */
	private String contactName;

	/**
	 * 回单地址
	 */
	private String reciveAddr;

	/**
	 * 公司名称（全称）
	 */
	private String companyName;

	/**
	 * 公司地址
	 */
	private String address;

	/**
	 * 联系人名称
	 */
	private String lineName;

	/**
	 * 联系人电话
	 */
	private String linePhone;

	/**
	 * 财务编码
	 */
	private String yongyouCode;

	/**
	 * 合同图片url
	 */
	private String contractUrl;

	/**
	 * 图片id
	 */
	private Long contractId;

	/**
	 * 回单人ID
	 */
	private Long receiptsUserId;

	/**
	 * 回单人姓名
	 */
	private String receiptsUserName;

	/**
	 * 客户类型：1 固定线路的客户，0 临时线路的客户
	 */
	private Integer custType;

	/**
	 * 回单地址的省份id
	 */
	private Long reciveProvinceId;

	/**
	 * 回单地址的市id
	 */
	private Long reciveCityId;

	/**
	 * 客户单号，多个用换行符分隔
	 */
	private String customNumber;

	/**
	 * 货品类型
	 */
	private Integer goodsType;

}
