package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.AccountDetails;
import com.youming.youche.finance.dto.AccountQueryDetailDto;
import com.youming.youche.finance.vo.AccountQueryDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-04-12
*/
public interface AccountDetailsMapper extends BaseMapper<AccountDetails> {

    List<AccountQueryDetailDto> getAccountQueryDetailsList(@Param("accountQueryDetailVo") AccountQueryDetailVo accountQueryDetailVo, @Param("businessNumbers") String businessNumbers);

    Integer getAccountQueryDetailsCount(@Param("accountQueryDetailVo") AccountQueryDetailVo accountQueryDetailVo, @Param("businessNumbers") String businessNumbers);

    Integer queryAccountDetailTable(@Param("tableName") String tableName);

    Integer tableIsExist(@Param("yyyyMM") String yyyyMM);

    void createTable(@Param("yyyyMM") String yyyyMM);

    AccountDetails selectTable(@Param("yyyyMM") String yyyyMM, @Param("id") Long id);

    Integer insertTable(@Param("details") AccountDetails details, @Param("yyyyMM") String yyyyMM);

    Integer updateTable(@Param("details") AccountDetails details, @Param("yyyyMM") String yyyyMM);
}
