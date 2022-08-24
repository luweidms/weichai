package com.youming.youche.order.domain.order;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@Data
@Accessors(chain = true)
public class BillReceive extends BaseDomain {

    private static final long serialVersionUID = 1L;


//    private Long receiveId;

    /**
     * 邮寄地址
     */
    private String address;

    /**
     * 市
     */
    private Integer cityId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 收件人
     */
    private String consignee;

    /**
     * 收件电话
     */
    private String consigneePhone;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 县/区
     */
    private Integer districtId;

    /**
     * 县/区名称
     */
    private String districtName;

    /**
     * 省
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 租户ID
     */
    private Long tenantId;


}
