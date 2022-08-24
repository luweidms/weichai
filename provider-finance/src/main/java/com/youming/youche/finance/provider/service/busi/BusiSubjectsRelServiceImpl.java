package com.youming.youche.finance.provider.service.busi;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.busi.IBusiSubjectsRelService;
import com.youming.youche.finance.domain.busi.BusiSubjectsRel;
import com.youming.youche.finance.provider.mapper.busi.BusiSubjectsRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 * @author Terry
 * @since 2022-03-08
 */
@DubboService(version = "1.0.0")
public class BusiSubjectsRelServiceImpl extends BaseServiceImpl<BusiSubjectsRelMapper, BusiSubjectsRel> implements IBusiSubjectsRelService {


    @Override
    public List<BusiSubjectsRel> feeCalculation(long businessId, List<BusiSubjectsRel> subjectsList) {

        return null;
    }

    @Override
    public List<BusiSubjectsRel> getBusiSubjectsRels() {
        return this.list();
    }

    @Override
    public BusiSubjectsRel createBusiSubjectsRel(long subjectsId, long amount) {
        BusiSubjectsRel busiSubjectsRel = new BusiSubjectsRel();
        busiSubjectsRel.setSubjectsId(subjectsId);
        busiSubjectsRel.setAmountFee(amount);
        return busiSubjectsRel;
    }
}
