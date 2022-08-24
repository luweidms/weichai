package com.youming.youche.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hzx
 * @date 2022/4/22 17:47
 */
@Data
public class QueryBacklistParamVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long backlistId;
    private String driverName;
    private String driverPhone;
    private String identification;
    private String plateNumber;
    private Long belongTenantId;
    private Integer backType;
    private Integer state;
    private String reason;
    private Date updateDate;
    private Long createUserId;
    private Date createDate;
    private Long tenantId;

    private Date createDateStart;
    private Date createDateEnd;
    private String tenantName;
    private String belongTenantName;

}
