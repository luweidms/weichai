package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IBusiSubjectsDtlService extends IBaseService<BusiSubjectsDtl> {
    /*查询业务科目明细，将现有业务科目细分*/
     List<BusiSubjectsDtl> queryBusiSubjectsDtl(Long businessId, Long subjectsId);
}
