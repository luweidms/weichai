package com.youming.youche.market.vo.youca;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 油卡管理表
 * </p>
 *
 * @author Terry
 * @since 2022-02-09
 */
@Data
public class CardManagementVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实体油卡卡号
     */
    private String oilCarNum;
    /**
     * 理论余额
     */
    private String cardBalance;
}
