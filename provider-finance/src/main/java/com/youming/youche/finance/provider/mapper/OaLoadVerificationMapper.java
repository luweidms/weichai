package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.OaLoadVerification;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * Mapper接口
 * </p>
 * @author Terry
 * @since 2022-03-09
 */
public interface OaLoadVerificationMapper extends BaseMapper<OaLoadVerification> {

    List<OaLoadVerification>  queryOaLoadVerificationsById(@Param("Lid") Long Lid);
}
