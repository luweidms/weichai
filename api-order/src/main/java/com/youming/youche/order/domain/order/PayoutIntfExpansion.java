package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PayoutIntfExpansion extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 发票抬头
     */
    private String billLookUp;
    /**
     * 支付单号
     */
    private String billSerialNumber;
    /**
     * 发起流水
     */
    private String bizSeq;
    /**
     * 业务单号
     */
    private String busiCode;
    /**
     * 代收人
     */
    private String collectionUserName;
    /**
     * 客户名称
     */
    private String customName;
    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;
    /**
     * 目的地
     */
    private Integer desRegion;
    /**
     * 文件id
     */
    private Long fileId;
    /**
     * 附件地址
     */
    private String fileUrl;
    /**
     * 流水号
     */
    private Long flowId;
    /**
     * 是否开票
     */
    private Integer isNeedBill;
    /**
     * 订单备注
     */
    private String orderRemark;
    /**
     * 部門id
     */
    private Long orgId;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 备注
     */
    private String remark;
    /**
     * 线路名称
     */
    private String sourceName;
    /**
     * 起始点
     */
    private Integer sourceRegion;
    /**
     * 车辆归属部门
     */
    private Long vehicleOrgId;


}
