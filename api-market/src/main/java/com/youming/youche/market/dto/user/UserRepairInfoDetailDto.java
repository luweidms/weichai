package com.youming.youche.market.dto.user;

import com.youming.youche.market.domain.user.UserRepairInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 维保审核详情
 *
 * @author hzx
 * @date 2022/3/12 17:43
 */
@Data
public class UserRepairInfoDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer zyCount;

    private UserRepairInfo repairInfo;

    private List<RepairItemsDto> repairItems;

    private List<Map> authList;


}
