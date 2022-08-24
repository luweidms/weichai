package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class RepairItemsWXOutDto  implements Serializable {

    private static final long serialVersionUID = 562228499675719330L;
    private Long amount;
    private String des;
    private String productName;
    private String repairItemName;
    private String repairPicIds;
    private String repairPicUrl;
    private Integer repairRootId;
    private String repairRootIdName;
    private Integer repairRootTwoId;
    private String repairRootTwoIdName;
    private Long univalence;
    private Integer workingHours;
    private Long itemsId;

    private String[] repairPicUrlArr;
}
