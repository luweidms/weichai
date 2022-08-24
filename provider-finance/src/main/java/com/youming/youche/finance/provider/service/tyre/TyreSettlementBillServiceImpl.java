package com.youming.youche.finance.provider.service.tyre;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.finance.api.tyre.ITyreSettlementBillService;
import com.youming.youche.finance.domain.tyre.TyreSettlementBill;
import com.youming.youche.finance.provider.mapper.tyre.TyreSettlementBillMapper;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 * 轮胎结算账单汇总 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-15
 */
@DubboService(version = "1.0.0")
public class TyreSettlementBillServiceImpl extends BaseServiceImpl<TyreSettlementBillMapper, TyreSettlementBill> implements ITyreSettlementBillService {

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Override
    public TyreSettlementBill getTyreSettlementBillVOByBusiCode(String busiCode) {
        LambdaQueryWrapper<TyreSettlementBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TyreSettlementBill::getBusiCode, busiCode);
        List<TyreSettlementBill> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void updTyreSettlementBillState(Long id, Integer state, String desc, String accessToken) {
        if (id < 0) {
            throw new BusinessException("操作失败,请确定参数");
        }
        TyreSettlementBill tyreSettlementBill = this.getById(id);
        if (null == tyreSettlementBill) {
            throw new BusinessException("账单信息不存在");
        }
        tyreSettlementBill.setTyrePayState(state);
        this.update(tyreSettlementBill);
        saveSysOperLog(SysOperLogConst.BusiCode.tyreSettlementBill, SysOperLogConst.OperType.Update, desc, accessToken, tyreSettlementBill.getId());
    }

    /**
     * 记录日志
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

}
