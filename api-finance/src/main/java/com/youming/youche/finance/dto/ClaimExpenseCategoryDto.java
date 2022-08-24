package com.youming.youche.finance.dto;


import com.youming.youche.finance.domain.ClaimExpenseInfo;
import lombok.Data;

import java.util.Date;

@Data
public class ClaimExpenseCategoryDto extends ClaimExpenseInfo {


//    private String stsString;
    private Double amountDouble;//金额
//
//    private String accidentTypeName;
//    private String accidentReasonName;
//    private String dutyDivideName;
//    private String accidentDivideName;
    private Long accUserId;

//    private List<String> fileUrl;//文件url
//    private List<String> fileName;//
//    private List<Long> fileId;//文件id

    private Boolean isHasPermission;// 是否有前线
    private String isNeedBillName;//是否需要开票名称
    private Date appDates;//时间
    /**
     * 车辆种类
     */
    private  Integer vehicleClass;
    private  String stateString;//状态名称
}
