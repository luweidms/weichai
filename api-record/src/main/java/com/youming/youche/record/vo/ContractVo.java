package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Date:2022/1/14
 */
@Data
public class ContractVo implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 合同编号
     */
    private String htBh;

    /**
     * 客户名称
     */
    private String htName;

    /**
     * 签订日期
     */
    private String htCreatetime;

    /**
     * 到期日期
     */
    private String htLefttime;

    /**
     * 操作时间
     */
    private String htUpdatetime;
    /**
     * 车队id
     */
    private Long tenantId;
}
