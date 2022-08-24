package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class OrderCostReportCardDto implements Serializable {
    private List<String> oilCardNum;
    private String etcCardNum;
    private List<String> etcCardNumList;
    public List<String> getOilCardNum() {
        return oilCardNum;
    }
    public void setOilCardNum(List<String> oilCardNum) {
        this.oilCardNum = oilCardNum;
    }
    public String getEtcCardNum() {
        return etcCardNum;
    }
    public void setEtcCardNum(String etcCardNum) {
        this.etcCardNum = etcCardNum;
    }

    public List<String> getEtcCardNumList() {
        return etcCardNumList;
    }

    public void setEtcCardNumList(List<String> etcCardNumList) {
        this.etcCardNumList = etcCardNumList;
    }
}
