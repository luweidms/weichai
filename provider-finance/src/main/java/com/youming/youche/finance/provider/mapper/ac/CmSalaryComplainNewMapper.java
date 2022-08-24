package com.youming.youche.finance.provider.mapper.ac;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.ac.CmSalaryComplainNew;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.dto.CmSalaryInfoDto;
import com.youming.youche.finance.vo.CmSalaryInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-04-18
*/
    public interface CmSalaryComplainNewMapper extends BaseMapper<CmSalaryComplainNew> {

    /**
     * 查询工资申诉
     *
     * @param salaryId
     * @return
     */
    List<CmSalaryComplainNew> getCmSalaryComplainNew(@Param("salaryId") long salaryId);

    /**
     * 查询工资申诉数量
     * @param salaryId
     * @return
     */
    Long getCmSalaryComplainNewCount(@Param("salaryId") long salaryId);

    /**
     * 查询工资列表新版
     * @param page
     * @param cmSalaryInfoDto
     * @return
     */
    Page<CmSalaryInfoVo>  queryCmSalaryInfo(Page<CmSalaryInfoVo> page,
                                      @Param("cmSalaryInfoDto") CmSalaryInfoDto cmSalaryInfoDto);

    }
