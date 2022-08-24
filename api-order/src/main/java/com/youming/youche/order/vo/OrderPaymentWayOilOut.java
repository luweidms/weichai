package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单模式油
 * @author dacheng
 *
 */
@Data
public class OrderPaymentWayOilOut implements Serializable {

	private static final long serialVersionUID = 6428784618809949300L;
	/*
	 * 订单成本模式油
	 */
	private Long costOil;
	/*
	 * 订单实报实销模式油
	 */
	private Long expenseOil;
	/*
	 * 订单承包模式油
	 */
	private Long contractOil;

}
