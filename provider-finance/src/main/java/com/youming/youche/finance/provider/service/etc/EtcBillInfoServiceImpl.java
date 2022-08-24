package com.youming.youche.finance.provider.service.etc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.etc.IEtcBillInfoService;
import com.youming.youche.finance.domain.etc.EtcBillInfo;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.provider.mapper.etc.EtcBillInfoMapper;
import com.youming.youche.finance.provider.utils.EtcConsts;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
@DubboService(version = "1.0.0")
public class EtcBillInfoServiceImpl extends BaseServiceImpl<EtcBillInfoMapper, EtcBillInfo> implements IEtcBillInfoService {

    @Override
    public void payForEtcBillWriteBack(PayoutIntf payoutIntf) {
        String busiCode = payoutIntf.getBusiCode();
        EtcBillInfo etcBillInfo = this.getEtcBillInfoByBusiCode(busiCode);
        if (etcBillInfo != null) {
            //判断当前数据状态是支付中的才更新
            if (etcBillInfo.getStatus() == EtcConsts.ETC_BILL_STATE.STS4 || etcBillInfo.getStatus() == EtcConsts.ETC_BILL_STATE.STS5) {
                if (etcBillInfo.getNopaySumFee() > 0) {//还有部分未支付
                    etcBillInfo.setStatus(EtcConsts.ETC_BILL_STATE.STS3);//部分支付
                } else {
                    etcBillInfo.setStatus(EtcConsts.ETC_BILL_STATE.STS2);//已支付
                }
                this.save(etcBillInfo);
            }
        }
    }

    @Override
    public EtcBillInfo getEtcBillInfoByBusiCode(String busiCode) {
        LambdaQueryWrapper<EtcBillInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EtcBillInfo::getBusiCode, busiCode);
        List<EtcBillInfo> list = this.list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
