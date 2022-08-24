package com.youming.youche.finance.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.OilTurnEntity;

import javax.xml.crypto.Data;
import java.util.Date;

/**
* <p>
    *  服务类
    * </p>
* @author luona
* @since 2022-04-20
*/
public interface IOilTurnEntityService extends IBaseService<OilTurnEntity> {

    /**
     * 获取OilTurnEntity数据
     * @param id
     * @return
     */
    OilTurnEntity getOilTurnEntity(Long id);

    int updateById(Long id, Integer state, Date verificationDate, Long noVerificationAmount);
}
