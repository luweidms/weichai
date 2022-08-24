//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.citys;

import java.io.Serializable;

/***
 * @Description:
 * @Author: luwei
 * @Date: 2022/7/27 10:37
 * @Param null:
 * @return: null
 * @Version: 1.0
 **/
public class District implements Serializable {
    private static final long serialVersionUID = -7407352204844541974L;
    private long id;
    private Long cityId;
    private String name;
    private String postCode;
    private String distType;

    public District() {
    }

    public District(Long cityId, String name, String postCode, String distType) {
        this.cityId = cityId;
        this.name = name;
        this.postCode = postCode;
        this.distType = distType;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostCode() {
        return this.postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getDistType() {
        return this.distType;
    }

    public void setDistType(String distType) {
        this.distType = distType;
    }
}
