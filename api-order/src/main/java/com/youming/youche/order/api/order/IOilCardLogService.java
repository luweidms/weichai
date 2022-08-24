package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OilCardLog;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IOilCardLogService extends IBaseService<OilCardLog> {

    /**
     * 修改新增是记录油卡使用记录
     *
     * @param cardId
     * @param balance
     * @param logDesc
     */
     void saveCardLog(Long cardId, Long balance, String logDesc,
                      Long orderId, LoginInfo baseUser);


    /**
     * 保存油卡日志
     * @param oilCardLog
     * @param type
     */
    void saveOrdUpdate(OilCardLog oilCardLog,Integer type);

}
