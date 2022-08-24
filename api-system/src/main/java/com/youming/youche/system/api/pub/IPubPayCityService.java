package com.youming.youche.system.api.pub;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.pub.PubPayCity;

import java.util.List;

/**
 * <p>
 * 平安城市表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
public interface IPubPayCityService extends IBaseService<PubPayCity> {

    /**
     * 获取省对应城市记录
     *
     * @param provId 省编码
     * @return
     */
    List<PubPayCity> getPubPayCityList(String provId);

}
