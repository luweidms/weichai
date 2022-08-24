package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderListInVo implements Serializable {
    private static final long serialVersionUID = 2662312746513853413L;
    /**
     * 始发省
     */
    private Integer sourceProvince;
    /**
     * 始发市
     */
    private Integer sourceRegion;
    /**
     * 始发县
     */
    private Integer sourceCounty;
    /**
     * 到达省
     */
    private Integer desProvince;//到达省市县
    /**
     * 到达市
     */
    private Integer desRegion;
    /**
     * 到达县
     */
    private Integer desCounty;
    /**
     * 车辆类型
     */
    private String vehicleClass;
    /**
     * 计价方式
     */
    private Integer priceEnum;
    /**
     * 需求车长
     */
    private String vehicleLengh;
    /**
     * 需求车辆类型
     */
    private Integer vehicleStatus;
    /**
     * 开单时间
     */
    private String beginOrderTime;
    /**
     * 结束时间
     */
    private String endOrderTime;
    /**
     * 靠台时间
     */
    private String beginDependTime;
    /**
     * 结束时间
     */
    private String endDependTime;
    /**
     * 接单时间  暂时不加
     */
    private String beginReceiveTime;
    /**
     * 接单结束时间  暂时不加
     */
    private String endReceiveTime;
    /**
     * 转单时间暂时不加
     */
    private String begIntegerransferTime;
    /**
     * 转单结束时间暂时不加
     */
    private String endTransferTime;
    /**
     * 客户名称
     */
    private String customName;
    /**
     * 订单状态
     */
    private String orderState;


    /**
     * 订单类型OrderType
     */
    private String orderType;
    /**
     * 是否支付预付款
     */
    private Integer amountFlag;
    /**
     * 订单号
     */
    private String orderIds;//
    /**
     * 查询类型    1 订单跟踪    2 回单审核  3 异常列表
     */
    private Integer selectType;//
    /**
     *是否为外发 1 是  2 不是    其他:所有
     */
    private Integer isOuter;
    /**
     *承运方
     */
    private String carrier;
    /**
     *是否历史  1是  其他:不是
     */
    private Integer isHis;
    /**
     *录入人
     */
    private String opName;
    /**
     *车牌号
     */
    private String plateNumber;
    /**
     *司机姓名
     */
    private String carUserName;
    /**
     *司机手机号
     */
    private String carUserPhone;
    /**
     *挂车车牌
     */
    private String trailerPlate;
    /**
     *审核来源
     */
    private Integer auditSource;
    /**
     * 归属部门Id
     */
    private String orgId;
    /**
     * 线路关键字
     */
    private String lineKey;
    /**
     * 审核权限
     */
    private Boolean todo;
    /**
     * 业务ID集合
     */
    private List<Long> busiId;

    /**
     * 是否有所有权限
     */
    private Boolean hasAllData;
    /**
     * 部门集合
     */
    private List<Long> orgIdList;

    /**
     * 异常类型
     */
    private Integer problemCondition;
    /**
     * 是否有异常 false 查全部  true 查有异常的数据
     */
    private boolean hasExc;
    /**
     * 是否支付到付款
     */
    private Integer arrivePaymentState;
    /**
     * 客户单号
     */
    private List<String> customNumber;

    private String  customNumbers;
    /**
     * 票据信息
     */
    private Integer isNeedBill;
    /**
     * 支付业务ID集合
     */
    private List<Long> preBusiIds;
    /**
     * 是否查询支付待处理
     */
    private Boolean preTodo;
    /**
     * 是否查询到付支付待处理
     */
    private Boolean arriveTodo;
    /**
     * 是否查询尾款待处理
     */
    private Boolean finalTodo;
    /**
     * 是否导出
     */
    private Boolean isExport;
    /**
     * /异常处理时间
     */
    private String beginProblemDealTime;
    /**
     * 异常结束处理时间
     */
    private String endProblemDealTime;
    /**
     *  租户id
     */
    private Long tenantId;

    /**
     *
     */
    private int isZeroPre;

    /**
     * 修改订单状态 0 修改 1 审核'
     */
    private  Integer orderUpdateState; //

    /**
     * 修改订单状态 0 修改 1 审核'
     */
    private  Integer orderUpdateStateTwo; //

    private Integer preTotalFee;


   private String begin;
   private String end;

}
