package com.youming.youche.record.dto.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DriverApplycordNum
 * @Package: com.youming.youche.dto.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/18 13:07
 * @company:
 */
@Data
public class DriverApplyRecordNumDto implements Serializable {

    /**
     *我邀请的数量
     */
    private Integer driverInvite;

    /**
     * 邀请我的数量
     */
    private Integer driverBeInvite;

}
