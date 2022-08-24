package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author hzx
 * @date 2022/3/31 11:04
 */
@Data
public class SysOperLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 操作员编号
     */
    private Long opId;

    /**
     * 操作人名称
     */
    private String opName;

    /**
     * 业务编码
     */
    private Integer busiCode;

    /**
     * 业务名称
     */
    private String busiName;

    /**
     * 操作名称:例如新增，修改
     */
    private String operTypeName;

    /**
     * 操作类型
     */
    private Integer operType;

    /**
     * 业务数据的主键
     */
    private Long busiId;

    /**
     * 操作的日志描述
     */
    private String operComment;

    /**
     * 1：APP-ANDROID2：APP-IOS3:WEB
     */
    private String channelType;

    /**
     * 租户ID
     */
    private Long tenantId;

    private Long operRecId;

    private String billId;

    private String objectId;

    private Integer objectType;

    private Integer operClass;

    private LocalDateTime operDate;

    private String operName;

    private Long operatorId;

    private String remarks;

    private Long userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
