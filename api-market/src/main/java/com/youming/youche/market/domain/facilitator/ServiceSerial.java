package com.youming.youche.market.domain.facilitator;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-07-11
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ServiceSerial extends BaseDomain {

    private static final long serialVersionUID = 1L;
    /**
     * 账单流水号
     */
    private String serialNumber;
    /**
     * 服务商id
     */
    private Long serviceUserId;
    /**
     * 车都id
     */
    private Long tenantId;


            /**
            * 流水号 业务单号
            */
    private String serialData;

            /**
            * 服务商名
            */
    private String serviceName;

            /**
            * 收款方手机号
            */
    private String mobile;

            /**
            * 车牌号码
            */
    private String plateNumber;

            /**
            * 司机名称
            */
    private String driverName;

            /**
            * 类型
            */
    private String typeName;

            /**
            * 金额
            */
    private Double amount;

            /**
            * 交易时间
            */
    private LocalDateTime time;


}
