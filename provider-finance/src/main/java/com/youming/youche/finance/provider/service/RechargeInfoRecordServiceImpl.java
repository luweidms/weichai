package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.IOilEntityService;
import com.youming.youche.finance.api.IRechargeInfoRecordService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.OilEntity;
import com.youming.youche.finance.domain.RechargeInfoRecord;
import com.youming.youche.finance.provider.mapper.RechargeInfoRecordMapper;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


/**
* <p>
    * 充值记录表 服务实现类
    * </p>
* @author WuHao
* @since 2022-04-15
*/
@DubboService(version = "1.0.0")
    public class RechargeInfoRecordServiceImpl extends BaseServiceImpl<RechargeInfoRecordMapper, RechargeInfoRecord> implements IRechargeInfoRecordService {

    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService;

    @Resource
    IOilEntityService iOilEntityService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService; // 日志

    @Override
    public RechargeInfoRecord getObjectById(Long soNbr) {
        QueryWrapper<RechargeInfoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("so_nbr",soNbr);
        List<RechargeInfoRecord> list = this.list(queryWrapper);
        if (list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public RechargeInfoRecord getRechargeInfoRecordByRechargeFlowId(String rechargeFlowId) {
        LambdaQueryWrapper<RechargeInfoRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RechargeInfoRecord::getRechargeFlowId, rechargeFlowId);
        queryWrapper.orderByAsc(RechargeInfoRecord::getId);
        List<RechargeInfoRecord> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new RechargeInfoRecord();
    }

    @Override
    public void createOilEntityInfo(String rechargeFlowId, boolean offline, String accessToken) {
        RechargeInfoRecord rir = this.getRechargeInfoRecordByRechargeFlowId(rechargeFlowId);
        ServiceInfo serviceInfo = iServiceInfoService.getServiceInfoById(rir.getServiceUserId());

        OilEntity oilEntity = new OilEntity();
        oilEntity.setOilCarNum(rir.getCardNum());
        oilEntity.setPreOilFee(-rir.getRechargeAmount());
        oilEntity.setCreationTime(LocalDateTime.now());
        oilEntity.setOilType(3);
        oilEntity.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
        oilEntity.setLineState(OrderAccountConst.OIL_ENTITY.LINE_STATE1);
        oilEntity.setTenantId(rir.getTenantId());
        oilEntity.setNoVerificateEntityFee(0L);
        oilEntity.setVoucherAmount(0L);
        if (offline) {
            oilEntity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE4);
        } else {
            oilEntity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE5);
        }
        oilEntity.setServiceName(serviceInfo.getServiceName());
        iOilEntityService.saveOrUpdate(oilEntity);
        oilEntity.setSoNbr(rir.getId());
        oilEntity.setBusiCode(rir.getRechargeFlowId());
        rir.setSoNbr(oilEntity.getId());
        rir.setState(OrderAccountConst.RECHARGE_OIL_CARD.PAY_STATE3);
        iOilEntityService.saveOrUpdate(oilEntity);
        this.saveOrUpdate(rir);
        //操作日志
        saveSysOperLog(SysOperLogConst.BusiCode.OilEntity, SysOperLogConst.OperType.Add,
                "圈退 " + CommonUtil.divide(oilEntity.getPreOilFee()) + "元", accessToken, oilEntity.getId());

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
