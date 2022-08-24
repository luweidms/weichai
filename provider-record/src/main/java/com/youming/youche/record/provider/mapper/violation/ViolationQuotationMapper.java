package com.youming.youche.record.provider.mapper.violation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.violation.ViolationQuotation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022/1/8 9:26
 */
public interface ViolationQuotationMapper extends BaseMapper<ViolationQuotation> {

    /**
     * 查询除了配置的服务商以外的报价数目
     *
     * @param recordId  违章记录ID
     * @param serviceId 服务商ID
     * @return
     */
    long getViolationQuotationCount(@Param("recordId") long recordId, @Param("serviceId") long serviceId);

    List<ViolationQuotation> selectOneByRecordId(@Param("recordId") Long recordId);
}
