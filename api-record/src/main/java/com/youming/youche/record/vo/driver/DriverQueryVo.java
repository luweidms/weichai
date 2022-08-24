package com.youming.youche.record.vo.driver;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version:
 * @Title: DriverQueryVo
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/23 10:20
 * @company:
 */
@Data
public class DriverQueryVo implements Serializable {

    private Long userId;

    private String loginAcct;

    private String linkman;

    private Integer carUserType;

    private String carUserTypeName;

    private Long attachTenantId;

    private String attachTenantName;

    private String attachTennantLinkman;

    private String attachTennantLinkPhone;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    private Long relId;

    private String authReason;

}
