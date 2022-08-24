package com.youming.youche.order.vo;

import com.youming.youche.order.domain.order.BillInfo;
import com.youming.youche.order.domain.order.BillReceive;

import java.io.Serializable;

public class BillInfoReceiveVo implements Serializable {
    private Long relId;
    private Long billInfoId;
    private Long receiveId;
    private Long tenantId;
    private Boolean deduction;

    /**
     * 开票信息
     */
    private BillInfo billInfo;
    /**
     * 收件人信息
     */
    private BillReceive billReceive;

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

    public Long getBillInfoId() {
        return billInfoId;
    }

    public void setBillInfoId(Long billInfoId) {
        this.billInfoId = billInfoId;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public BillInfo getBillInfo() {
        return billInfo;
    }

    public void setBillInfo(BillInfo billInfo) {
        this.billInfo = billInfo;
    }

    public BillReceive getBillReceive() {
        return billReceive;
    }

    public void setBillReceive(BillReceive billReceive) {
        this.billReceive = billReceive;
    }

    public Boolean getDeduction() {
        return deduction;
    }

    public void setDeduction(Boolean deduction) {
        this.deduction = deduction;
    }
}
