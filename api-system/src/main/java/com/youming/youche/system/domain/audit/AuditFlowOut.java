package com.youming.youche.system.domain.audit;

import com.youming.youche.system.dto.AuditNodePageDto;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * @version:
 * @Title: AuditFlowOut
 * @Package: com.youming.youche.system.domain.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 10:53
 * @company:
 */
public class AuditFlowOut implements Comparator<AuditFlowOut>, Serializable {

    /**
     * 审核业务编码
     */
    private String auditCode;

    /**
     * 审核业务名称
     */
    private String auditName;

    /**
     * 审核流程节点
     */
    private List<AuditNodePageDto> auditNodes;

    /**
     * 是否退回发起人
     */
    private Integer flag;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * 排序号
     */
    private Integer sortId;

    public String getAuditCode() {
        return auditCode;
    }

    public void setAuditCode(String auditCode) {
        this.auditCode = auditCode;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName;
    }

    public List<AuditNodePageDto> getAuditNodes() {
        return auditNodes;
    }

    public void setAuditNodes(List<AuditNodePageDto> auditNodes) {
        this.auditNodes = auditNodes;
    }


    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((auditCode == null) ? 0 : auditCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuditFlowOut other = (AuditFlowOut) obj;
        if (auditCode == null) {
            if (other.auditCode != null)
                return false;
        } else if (!auditCode.equals(other.auditCode))
            return false;
        return true;
    }

    @Override
    public int compare(AuditFlowOut arg0, AuditFlowOut arg1) {
        if(arg0.getSortId()==null) {
            return -1;
        }
        if(arg1.getSortId()==null) {
            return 1;
        }
        return arg0.getSortId()>arg1.getSortId()?1:-1;
    }

}
