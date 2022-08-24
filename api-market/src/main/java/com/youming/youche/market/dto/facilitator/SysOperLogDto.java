package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysOperLogDto implements Serializable {
    /**
     * 业务编码
     */
    private Integer busiCode;
    /**
     * 业务主键
     */
    private Long busiId;
    /**
     * 是否升序
     */
    private Boolean ifAsc;
    /**
     * 审核的业务编码
     */
    private String auditCode;
    /**
     * 审核业务的busiId
     */
    private Long auditBusiId;
}
