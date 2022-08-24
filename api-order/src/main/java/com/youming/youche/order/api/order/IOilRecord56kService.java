package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OilRecord56k;
import com.youming.youche.order.dto.ParametersNewDto;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IOilRecord56kService extends IBaseService<OilRecord56k> {


    /**
     * 消费油
     * @param orderNum
     * @param tenantId
     * @throws Exception
     */
    void consumeOil(String orderNum, Long amount, Long tenantId,
                    ParametersNewDto inParam, LoginInfo user);

}
