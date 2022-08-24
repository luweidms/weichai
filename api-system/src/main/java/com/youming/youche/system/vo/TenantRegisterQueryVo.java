package com.youming.youche.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 车队注册及审核列表页的查询参数
 */
@Data
public class TenantRegisterQueryVo implements Serializable {

    private String startDate;

    private String endDate;

    private String linkPhone;

    private String companyName;

    private String actualController;
    /**
     * 审核状态：1-待审核 2-审核通过 3-审核不通过
     */
    private Integer auditState;

}
