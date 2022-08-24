package com.youming.youche.components.workbench;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/3 10:24
 */
public class WorkbenchDto implements Serializable {

    private static final long serialVersionUID = -7835620258560464609L;

    private Long tenantId;

    private Long userInfoId;

    private Long count;

    private Double price;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Long userInfoId) {
        this.userInfoId = userInfoId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


}
