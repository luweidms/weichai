package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderAgingListInVo implements Serializable {


    private static final long serialVersionUID = -5261212876673395707L;


    private Long orderId;
    private Integer orderType;
    private Integer orderState;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 公司名称
     */
    private String companyName;
    private String opName;
    private Integer auditState;
    private Integer selectType;
    private Integer desRegion;
    private Integer sourceRegion;
    private Boolean todo;
    /**
     * 时效罚款id
     */
    private List<Long> agingIds;
    private List<Long> appealIds;
    private Long orgId;
    private Boolean hasAllData;//是否有所有权限
    private List<Long> orgIdList;//部门集合
    private Boolean isWx;

    private String beginDependTime;

    private String endDependTime;


}
