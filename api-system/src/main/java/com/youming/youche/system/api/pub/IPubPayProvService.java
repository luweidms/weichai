package com.youming.youche.system.api.pub;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.pub.PubPayProv;
import com.youming.youche.system.dto.pub.PubPayDto;

import java.util.List;

/**
 * <p>
 * 平安省份表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
public interface IPubPayProvService extends IBaseService<PubPayProv> {

    /**
     * 省份记录
     */
    List<PubPayProv> getPubPayProvList();

    /**
     * 省市县区数据(10056)
     */
    List<PubPayDto> queryAddressInfo();

}
