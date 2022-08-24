package com.youming.youche.finance.provider.mapper.ac;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.ac.CmSalarySendInfo;

import java.util.List;

/**
* <p>
* 驾驶员工资发送信息表Mapper接口
* </p>
* @author zengwen
* @since 2022-06-29
*/
    public interface CmSalarySendInfoMapper extends BaseMapper<CmSalarySendInfo> {

        List<CmSalarySendInfo> selectSalaryList(Long salaryid);
    }
