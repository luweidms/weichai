package com.youming.youche.finance.provider.mapper.ac;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.ac.OrderDriverSubsidyInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-04-30
*/
    public interface OrderDriverSubsidyInfoMapper extends BaseMapper<OrderDriverSubsidyInfo> {

        List<OrderDriverSubsidyInfo> selectBySendId(@Param("sendId") Long send);
    }
