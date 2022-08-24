package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.dto.order.PayoutIntfDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IBusiSubjectsRelService extends IBaseService<BusiSubjectsRel> {


    /**
     * 创建费用科目
     * @param subjectsId
     * @param amount
     * @return
     * @throws Exception
     */
    BusiSubjectsRel createBusiSubjectsRel(Long subjectsId, Long amount);


    /**
     * 算费接口(金额按分计算)
     * @param businessId  业务ID
     * @param subjectsList 科目ID和费用集合  subjectsId科目、amountFee费用（单位分）
     * @return List<BusiSubjectsRel>
     * @throws Exception
     */
    List<BusiSubjectsRel> feeCalculation(Long businessId, List<BusiSubjectsRel> subjectsList);


    /**
     * 查询业务科目关系
     * @param businessId
     * @param tenantId
     * @return
     * @throws Exception
     */
     List<BusiSubjectsRel> getBusiSubjectsRel(Long businessId,Long tenantId);

    /**
     * @Description: 该函数的功能描述:油到期支付成功后回调
     */
    void paySucessOilCallBack(PayoutIntfDto payoutIntfDto);

    /**
     * 获取业务科目信息
     * @return
     */
    List<BusiSubjectsRel> getBusiSubjectsRelList();

}
