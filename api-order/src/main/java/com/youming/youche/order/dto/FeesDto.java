package com.youming.youche.order.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *       map.put("virtualFee", virtualFee);
 *         map.put("cashFee", cashFee);
 *         map.put("etcFee", etcFee);
 *         map.put("entityOilFee", entityOilFee);
 *         map.put("totalFee", totalFee);
 * */
@Data
@RequiredArgsConstructor(staticName = "of")
@Accessors(chain = true)
public class FeesDto implements Serializable {
        /**
         * //预付虚拟油
         */
        private Long virtualFee;
        /**
         * 现金金额
         */
        private Long cashFee;
        /**
         * etc金额
         */
        private Long etcFee;
        /**
         * 实体油卡金额单位(分)
         */
        private Long entityOilFee;
        /**
         * 中标价
         */
        private Long totalFee;
}
