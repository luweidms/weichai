package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoQueryOBMDriversVo
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/16 18:17
 * @company:
 */
@Data
public class DoQueryOBMDriversVo implements Serializable {

    /**
     * 开始时间
     */
    private String dateBegin;
    /**
     * 结束时间
     */
    private String dateEnd;

    /**
     * 认证状态
     */
    private Integer state;

    private Integer attach;

    private String attachTenantName;

    private String loginAcct;

    private String linkman;

    private String attachTenantLinkman;

    private String attachTenantLinkPhone;

    private Boolean hasVer;

    private Integer pageSize;

    private Integer page;

    private String createDate;

}
