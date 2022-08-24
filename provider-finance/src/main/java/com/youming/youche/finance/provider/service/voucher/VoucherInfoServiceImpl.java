package com.youming.youche.finance.provider.service.voucher;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.IRechargeInfoRecordService;
import com.youming.youche.finance.api.voucher.IVoucherInfoService;
import com.youming.youche.finance.domain.RechargeInfoRecord;
import com.youming.youche.finance.domain.voucher.VoucherInfo;
import com.youming.youche.finance.provider.mapper.voucher.VoucherInfoMapper;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.IServiceProductVerService;
import com.youming.youche.market.api.youka.IVoucherInfoRecordService;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.ServiceProductVer;
import com.youming.youche.market.domain.youka.VoucherInfoRecord;
import com.youming.youche.order.api.order.IOilEntityService;
import com.youming.youche.order.domain.order.OilEntity;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.youming.youche.finance.commons.util.DateUtil.DATETIME_FORMAT;


/**
 * <p>
 * 代金券信息表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-15
 */
@DubboService(version = "1.0.0")
public class VoucherInfoServiceImpl extends BaseServiceImpl<VoucherInfoMapper, VoucherInfo> implements IVoucherInfoService {

    @Resource
    IRechargeInfoRecordService iRechargeInfoRecordService;

    @DubboReference(version = "1.0.0")
    IServiceProductService iServiceProductService;

    @DubboReference(version = "1.0.0")
    IServiceProductVerService iServiceProductVerService;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService; // 日志

    @DubboReference(version = "1.0.0")
    IOilEntityService oilEntityService;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    IVoucherInfoRecordService iVoucherInfoRecordService;

    @Override
    public boolean judgeRechargeIsNeedWithdrawal(String busiCode, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(busiCode)) {
            throw new BusinessException("请输入busiCode值");
        }
        Date updateDate = new Date();
        RechargeInfoRecord info = iRechargeInfoRecordService.getRechargeInfoRecordByRechargeFlowId(busiCode);
        if (info == null) {
            throw new BusinessException("根据busiCode值：" + busiCode + " 未找到充值记录");
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

        ServiceProduct serviceProduct = iServiceProductService.getServiceProduct(info.getCardNum(), info.getCardType(), dateTimeFormatter.format(info.getCreateTime()));
        if (serviceProduct == null) {
            throw new BusinessException("根据卡号：" + info.getCardNum() + "卡号类型：" + info.getCardType() + " 未找到产品信息");
        }
        //更改母卡余额
        long parentAccountBalance = (serviceProduct.getParentAccountBalance() == null ? 0L : serviceProduct.getParentAccountBalance());
        saveSysOperLog(SysOperLogConst.BusiCode.servicePorduct, SysOperLogConst.OperType.Add, "减少母卡余额,金额：" + (double) info.getPayCash() / 100, accessToken, serviceProduct.getId());
        boolean isNeedWithdrawal = false;
        info.setUpdateTime(LocalDateTime.now());
        info.setUpdateOpId(loginInfo.getId());
        info.setState(OrderAccountConst.RECHARGE_OIL_CARD.PAY_STATE2);
        //油卡充值核销记录
        OilEntity entity = oilEntityService.getById(info.getSoNbr());
        if (entity == null) {
            throw new BusinessException("根据油卡号：" + info.getCardNum() + ",批次号：" + info.getId() + " 未找到油卡核销记录");
        }
        entity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE3);
        oilEntityService.saveOrUpdate(entity);
        SysCfg sysCfg = new SysCfg();
        SysCfg sysCfgNew = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(OrderAccountConst.RECHARGE_OIL_CARD.RECHARGE_WITHDRAWAL_THRESHOLD));
        if (null != sysCfgNew && (Integer.parseInt("0") == -1 || Integer.parseInt("0") == (sysCfg.getCfgSystem()))) {
            sysCfg = sysCfgNew;
        }
        String threshold = sysCfg.getCfgValue();
        if (StringUtils.isBlank(threshold)) {
            throw new BusinessException("未找到系统配置是否提现阈值参数");
        }
        long thresholdAmount = Long.parseLong(threshold);
        if ((parentAccountBalance - info.getRechargeAmount().longValue()) < thresholdAmount) {
            info.setNoRechargeAmount(info.getRechargeAmount());
            isNeedWithdrawal = true;
        } else {
            info.setNoRechargeAmount(0L);
            isNeedWithdrawal = false;
            serviceProduct.setParentAccountBalance(parentAccountBalance - info.getRechargeAmount().longValue());
            iServiceProductService.saveOrUpdate(serviceProduct);
            ServiceProductVer spv = new ServiceProductVer();
            BeanUtil.copyProperties(serviceProduct, spv);
//            spv.setOpDate(new Date());
            iServiceProductVerService.save(spv);
        }
        iRechargeInfoRecordService.saveOrUpdate(info);//充值记录
        saveSysOperLog(SysOperLogConst.BusiCode.rechargeInfoRecord, SysOperLogConst.OperType.Add, "打款成功", accessToken, info.getId());
        return isNeedWithdrawal;
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

    @Override
    public List<VoucherInfoRecord> getVoucherInfoRecord(Long id) {
        QueryWrapper<VoucherInfoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("recharge_info_record_id",id);
        List<VoucherInfoRecord> list = iVoucherInfoRecordService.list(queryWrapper);
        return list;
    }
}
