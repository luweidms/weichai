package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderAgingAppealInfoService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.provider.mapper.order.OrderAgingAppealInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderAgingInfoMapper;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.dto.AuditCallbackDto;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 时效罚款申诉表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-28
 */
@DubboService(version = "1.0.0")
@Service
public class OrderAgingAppealInfoServiceImpl extends BaseServiceImpl<OrderAgingAppealInfoMapper, OrderAgingAppealInfo> implements IOrderAgingAppealInfoService {

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;

    @Resource
    OrderAgingInfoMapper orderAgingInfoMapper;

    @Lazy
    @Autowired
    IOrderInfoService orderinfoservice;

    @Autowired
    IOrderFeeService orderFeeService;

    @Autowired
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Override
    public OrderAgingAppealInfo getAppealInfoBYAgingId(Long agingId, boolean isApp) {
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        OrderAgingAppealInfo info = getAppealInfoBYAgingId(agingId);
        if (info != null && !isApp) {
            if (StringUtils.isNotBlank(info.getImgUrls())) {
                String[] imgUrlArr = info.getImgUrls().split(",");
                for (int i = 0; i < imgUrlArr.length; i++) {
                    try {
                        imgUrlArr[i] = client.getHttpURL(imgUrlArr[i]).split("\\?")[0];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                info.setImgUrlArr(imgUrlArr);
            }
            if (info.getAuditSts() != null && info.getAuditSts() >= 0) {
                info.setAuditStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue(EnumConsts.SysStaticData.PRO_STATE, info.getAuditSts()+"").getCodeName());
            }
        }
        return info;
    }

    @Override
    public OrderAgingAppealInfo getAppealInfoBYAgingId(Long agingId) {
        if (agingId == null || agingId <= 0) {
            throw new BusinessException("时效ID为空!");
        }

        LambdaQueryWrapper<OrderAgingAppealInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAgingAppealInfo::getAgingId, agingId);

        List<OrderAgingAppealInfo> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    @Transactional
    public void verifyFirst(Long appealId, String verifyDesc, Long dealFinePrice, String accessToken) {
        if (appealId == null || appealId <= 0) {
            throw new BusinessException("未找到该申诉信息，请联系客服！");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<OrderAgingAppealInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAgingAppealInfo::getId, appealId);
        OrderAgingAppealInfo info = baseMapper.selectOne(queryWrapper);
        if (info == null) {
            log.error("未找到该申诉信息，请联系客服！申诉ID["+appealId+"]");
            throw new BusinessException("未找到该申诉信息，请联系客服！");
        }
        if (dealFinePrice == null || dealFinePrice < 0) {
            throw new BusinessException("处理金额不正确，请重新填写！");
        }
        info.setDealFinePrice(dealFinePrice);
        info.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
        baseMapper.updateById(info);
        // 审核流程
        AuditCallbackDto auditCallbackDto = auditSettingService.sure(AuditConsts.AUDIT_CODE.AGING_APPEAL_CODE, info.getId(),
                verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);
        if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()){
            // 审核成功
            this.sucess(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
        }else {
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.AgingInfo, info.getAgingId(), SysOperLogConst.OperType.Update, loginInfo.getName() + "审核通过,[" + verifyDesc + "]");
        }
    }

    @Override
    public void verifyFail(Long appealId, String verifyDesc,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (appealId == null || appealId <= 0) {
            throw new BusinessException("未找到该申诉信息，请联系客服！");
        }
        OrderAgingAppealInfo info = super.getById(appealId);
        if (info == null) {
            log.error("未找到该申诉信息，请联系客服！申诉ID["+appealId+"]");
            throw new BusinessException("未找到该申诉信息，请联系客服！");
        }
        info.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN);
        info.setAuditDate(LocalDateTime.now());
        this.saveOrUpdate(info);
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.AgingInfo, info.getAgingId(), SysOperLogConst.OperType.Update, loginInfo.getName() + "审核不通过,["+verifyDesc+"]");
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String accessToken) {
        this.success(busiId, desc,false, accessToken);
    }

    @Override
    public void success(Long appealId, String verifyDesc, Boolean isFirst, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (appealId == null || appealId <= 0) {
            throw new BusinessException("未找到该申诉信息，请联系客服！");
        }
        LambdaQueryWrapper<OrderAgingAppealInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAgingAppealInfo::getId, appealId);
        OrderAgingAppealInfo info = baseMapper.selectOne(queryWrapper);
        if (info == null) {
            log.error("未找到该申诉信息，请联系客服！申诉ID["+appealId+"]");
            throw new BusinessException("未找到该申诉信息，请联系客服！");
        }
        info.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.END);
        info.setAuditDate(LocalDateTime.now());
        // 修改时效罚款申诉信息：审核状态以及审核时间
        baseMapper.updateById(info);
        OrderAgingInfo agingInfo = orderAgingInfoMapper.selectById(info.getAgingId());
        if (agingInfo == null) {
            log.error("未找到时效罚款信息，请联系客服！时效ID["+info.getAgingId()+"]");
            throw new BusinessException("未找到时效罚款信息，请联系客服！");
        }
        //申诉通过金额至为0
        agingInfo.setFinePrice(info.getDealFinePrice());
        orderAgingInfoMapper.updateById(agingInfo);

        OrderInfo orderInfo = orderinfoservice.getOrder(agingInfo.getOrderId());
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(agingInfo.getOrderId());

        //同步支付中心
        orderFeeService.synPayCenterUpdateOrderOrProblemInfo(orderInfo, orderScheduler);
        if (!isFirst) {
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.AgingInfo, agingInfo.getId(),  SysOperLogConst.OperType.Update, loginInfo.getName() + "审核通过,["+verifyDesc+"]");
        }
    }
}
