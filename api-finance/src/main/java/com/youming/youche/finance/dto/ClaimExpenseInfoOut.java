package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClaimExpenseInfoOut implements Serializable {
    private String stsString;
    private Double amountDouble;
    private String orgName;

    private String accidentTypeName;
    private String accidentReasonName;
    private String dutyDivideName;
    private String accidentDivideName;
    private Long accUserId;

    private List<String> fileUrl;//文件url
    private List<String> fileName;//
    private List<Long> fileId;//文件id

    private Boolean isHasPermission;
    private String isNeedBillName;
}
