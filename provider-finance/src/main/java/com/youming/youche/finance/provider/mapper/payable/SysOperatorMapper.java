package com.youming.youche.finance.provider.mapper.payable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.payable.SysOperator;
import org.apache.ibatis.annotations.Param;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-04-23
*/
    public interface SysOperatorMapper extends BaseMapper<SysOperator> {


    SysOperator getSysOperatorByUserIdOrPhone(@Param("userId") Long userId, @Param("billId") String billId);
    }
