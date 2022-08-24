package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: doQueryDriversVo
 * @Package: com.youming.youche.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/18 13:17
 * @company:
 */
@Data
public class DoQueryDriversVo implements Serializable {

    /**
     *司机类型 (-1 全部；1:公司自有司机；2:业务招商司机；3:临时外调司机；4:外来挂靠司机)
     */
    private Integer carUserType;
    /**
     * 司机账号
     */
    private String loginAcct;
    /**
     *归属车队
     */
    private String attachTenantName;
    /**
     * 司机名称
     */
    private String linkman;

    /**
     *车队联系人
     */
    private String attachTennantLinkman;

    /**
     *车队电话
     */
    private String attachTennantLinkPhone;
    /**
     * 认证状态（-1:全部；1:未认证；2:已认证）
     */
    private Integer state;

    /**
     * 审核状态 (null:待审核；非空:审核)
     */
    private String hasVer;

    /**
     * 是否是运营账号(null:否；非空:是)
     */
    private String onlyC;

    /**
     *驾驶证(null:否；非空:是)
     */
    private String driverLicenseExpired;

    /**
     *从业资格证 (null:否；非空:是)
     */
    private String qcCertiExpired;

    private Long driverUserId;

    private Long queryTenantId;

}
