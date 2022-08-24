//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.citys;

import com.youming.youche.util.GB2Alpha;

import java.io.Serializable;

/***
 * @Description: 城市实体类
 * @Author: luwei
 * @Date: 2022/7/27 10:35
 * @Param null:
 * @return: null
 * @Version: 1.0
 **/
public class City implements Serializable {
    private static final long serialVersionUID = -7835620258560464609L;
    /**
     * 主键id
     */
    private long id;
    /**
     * 省份id
     */
    private Long provId;
    /**
     * 城市名称
     */
    private String name;
    /**
     * 区域编码
     */
    private String areaCode;
    /**
     * 城市类型
     */
    private String cityType;
    /**
     * 城市首字母大写拼接
     */
    private String nameAlpha;

    public City() {
    }

    public City(Long provId, String name, String areaCode, String cityType) {
        this.provId = provId;
        this.name = name;
        this.areaCode = areaCode;
        this.cityType = cityType;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getProvId() {
        return this.provId;
    }

    public void setProvId(Long provId) {
        this.provId = provId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCityType() {
        return this.cityType;
    }

    public void setCityType(String cityType) {
        this.cityType = cityType;
    }

    public String getNameAlpha() {
        if (this.name == null) {
            return null;
        } else {
            this.nameAlpha = (new GB2Alpha()).String2Alpha(this.name);
            return this.nameAlpha;
        }
    }

    public void setNameAlpha(String nameAlpha) {
        this.nameAlpha = nameAlpha;
    }


}
