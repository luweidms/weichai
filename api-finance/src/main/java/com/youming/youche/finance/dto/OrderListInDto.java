package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class OrderListInDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2662312746513853413L;
    private Integer sourceProvince;
    private Integer sourceRegion;
    private Integer sourceCounty;
    private Integer desProvince;//到达省市县
    private Integer desRegion;
    private Integer desCounty;
    private Integer vehicleClass;
    private Integer priceEnum;//计价方式
    private String vehicleLengh;//需求车长
    private Integer vehicleStatus;//需求车辆类型
    private String beginOrderTime;//开单时间
    private String endOrderTime;
    private String beginDependTime;//靠台时间
    private String endDependTime;
    private String beginReceiveTime;//接单时间  暂时不加
    private String endReceiveTime;
    private String begIntegerransferTime;//转单时间  暂时不加
    private String endTransferTime;
    private String customName;//客户名称
    private Integer orderState;//订单状态
    private Integer orderType;//订单类型
    private Integer amountFlag;//是否支付预付款
    private String orderIds;//订单号
    private Integer selectType;//查询类型    1 订单跟踪    2 回单审核  3 异常列表
    private Integer isOuter;//是否为外发 1 是  2 不是    其他:所有
    private String carrier;//承运方
    private Integer isHis;//是否历史  1是  其他:不是
    private String opName;//录入人
    private String plateNumber;//车牌号
    private String carUserName;//司机姓名
    private String carUserPhone;//司机手机号
    private String trailerPlate;//挂车车牌
    private Integer auditSource;//审核来源
    private Long orgId;//归属部门Id
    private String lineKey;//线路关键字
    private Boolean todo;//审核权限
    private List<Long> busiId;//业务ID集合

    private Boolean hasAllData;//是否有所有权限
    private List<Long> orgIdList;//部门集合

    private Integer problemCondition;//异常类型
    private boolean hasExc;//是否有异常 false 查全部  true 查有异常的数据
    private Integer arrivePaymentState;//是否支付到付款
    private String customNumber;//客户单号
    private Integer isNeedBill;//票据信息
    private List<Long> preBusiIds;//支付业务ID集合
    private Boolean preTodo;//是否查询支付待处理
    private Boolean arriveTodo;//是否查询到付支付待处理
    private Boolean finalTodo;//是否查询尾款待处理
    private Boolean isExport;//是否导出

    private String beginProblemDealTime;//异常处理时间
    private String endProblemDealTime;
    private Long tenantId;


    private int isZeroPre;
}
