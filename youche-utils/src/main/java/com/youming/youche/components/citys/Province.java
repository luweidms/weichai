//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.citys;

import java.io.Serializable;

public class Province implements Serializable {
    private static final long serialVersionUID = -5492550628407550469L;
    private long id;
    private String name;
    private Integer sortId;
    private String provType;

    public Province() {
    }

    public Province(String name, Integer sortId, String provType) {
        this.name = name;
        this.sortId = sortId;
        this.provType = provType;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortId() {
        return this.sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public String getProvType() {
        return this.provType;
    }

    public void setProvType(String provType) {
        this.provType = provType;
    }
}
