package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.OilTurnEntity;
import org.apache.ibatis.annotations.Param;
import java.util.Date;


/**
* <p>
* Mapper接口
* </p>
* @author luona
* @since 2022-04-20
*/
public interface OilTurnEntityMapper extends BaseMapper<OilTurnEntity> {
    /**
     * 获取OilTurnEntity数据
     * @param id
     * @return
     */
    OilTurnEntity getOilTurnEntity(Long id);


    int updateById(@Param("id") Long id, @Param("state") Integer state, @Param("verificationDate") Date verificationDate, @Param("noVerificationAmount") Long noVerificationAmount);
}
