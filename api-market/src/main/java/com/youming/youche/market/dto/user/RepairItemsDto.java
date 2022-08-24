package com.youming.youche.market.dto.user;

import com.youming.youche.market.commons.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzx
 * @date 2022/3/12 17:47
 */
@Data
public class RepairItemsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private long itemsId;
    private long repairId;
    private String repairCode;
    private String repairRootId;
    private String repairRootTwoId;
    private String repairItemName;
    private Long amount;
    private Double amountDouble;
    private String des;
    private String repair1PicUrl;
    private String repair2PicUrl;
    private String repair3PicUrl;
    private String repair4PicUrl;
    private String repair5PicUrl;
    private String productName;
    private Long univalence;
    private Float workingHours;
    private List<String> repairPicUrls;
    private String repairRootIdStr;
    private Long tenantId;

    public Double getAmountDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getAmount(), 2);
    }

}
