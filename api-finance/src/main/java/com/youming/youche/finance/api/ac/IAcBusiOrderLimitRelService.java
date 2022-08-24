package com.youming.youche.finance.api.ac;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.ac.AcBusiOrderLimitRel;

import java.util.List;

/**
 * <p>
 * 支付限制表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-14
 */
public interface IAcBusiOrderLimitRelService extends IBaseService<AcBusiOrderLimitRel> {

    /**
     * 获取所有支付限制记录
     *
     * @return
     */
    List<AcBusiOrderLimitRel> getBusiOrderLimitRels();

}
