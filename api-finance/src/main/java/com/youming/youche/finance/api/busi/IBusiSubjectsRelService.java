package com.youming.youche.finance.api.busi;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.busi.BusiSubjectsRel;

import java.util.List;

/**
 * <p>
 *   算费接口
 *  服务类
 * </p>
 * @author Terry
 * @since 2022-03-08
 */
public interface IBusiSubjectsRelService extends IBaseService<BusiSubjectsRel> {

    /**
     * 算费接口(金额按分计算)
     * @param businessId  业务ID
     * @param subjectsList 科目ID和费用集合  subjectsId科目、amountFee费用（单位分）
     * @return List<BusiSubjectsRel>
     * @throws Exception
     */
    List<BusiSubjectsRel> feeCalculation(long businessId, List<BusiSubjectsRel> subjectsList);

    /**
     * 获取所有费用科目
     *
     * @return
     */
    List<BusiSubjectsRel> getBusiSubjectsRels();

    /**
     * 创建费用科目
     */
    BusiSubjectsRel createBusiSubjectsRel(long subjectsId, long amount);

}
