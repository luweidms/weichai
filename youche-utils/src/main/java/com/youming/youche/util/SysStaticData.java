//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.util;

import java.io.Serializable;

/**
 * 使用{@link com.youming.youche.commons.domain.SysStaticData}
 * */
@Deprecated
public class SysStaticData implements Serializable {
    private static final long serialVersionUID = 4189622966130941924L;
    private long flowId;
    private long codeId;
    private long tenantId = -1L;
    private String codeType;
    private String codeValue;
    private String codeName;
    private String codeDesc;
    private String codeTypeAlias;
    private Integer sortId;
    private String state;

    public SysStaticData() {
    }

    public SysStaticData(long codeId) {
        this.codeId = codeId;
    }

    public SysStaticData(long codeId, String codeType, String codeValue, String codeName, String codeDesc, String codeTypeAlias, Integer sortId, String state) {
        this.codeId = codeId;
        this.codeType = codeType;
        this.codeValue = codeValue;
        this.codeName = codeName;
        this.codeDesc = codeDesc;
        this.codeTypeAlias = codeTypeAlias;
        this.sortId = sortId;
        this.state = state;
    }

    public long getFlowId() {
        return this.flowId;
    }

    public void setFlowId(long flowId) {
        this.flowId = flowId;
    }

    public long getCodeId() {
        return this.codeId;
    }

    public void setCodeId(long codeId) {
        this.codeId = codeId;
    }

    public String getCodeType() {
        return this.codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCodeValue() {
        return this.codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getCodeTypeAlias() {
        return this.codeTypeAlias;
    }

    public void setCodeTypeAlias(String codeTypeAlias) {
        this.codeTypeAlias = codeTypeAlias;
    }

    public Integer getSortId() {
        return this.sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }
}
