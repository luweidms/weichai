package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsDto;
import com.youming.youche.order.provider.mapper.order.OilRechargeAccountDetailsMapper;
import com.youming.youche.order.vo.OilRechargeAccountDetailsVo;
import com.youming.youche.record.common.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.hssf.record.DVALRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class OilRechargeAccountDetailsServiceImpl extends BaseServiceImpl<OilRechargeAccountDetailsMapper, OilRechargeAccountDetails> implements IOilRechargeAccountDetailsService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    IOrderSchedulerService iOrderSchedulerService;
    @Resource
    IOrderSchedulerHService iOrderSchedulerHService;

    @Override
    public List<OilRechargeAccountDetails> getOilRechargeAccountDetail(Long userId, String pinganAccId, Long sourceUserId, Integer sourceType, String vehicleAffiliation) {
        LambdaQueryWrapper<OilRechargeAccountDetails> lambda=new QueryWrapper<OilRechargeAccountDetails>().lambda();
        lambda.eq(OilRechargeAccountDetails::getUserId,userId);
        if((sourceType== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2||sourceType== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3)
                && StringUtils.isBlank(pinganAccId)){
            throw new BusinessException("现金/授信充值类型不允许虚拟账户为空");
        }
        if(StringUtils.isNotBlank(pinganAccId)){
            lambda.eq(OilRechargeAccountDetails::getPinganAccId,pinganAccId);
        }
        if(sourceUserId!=null&&sourceUserId>0L){
            lambda.eq(OilRechargeAccountDetails::getSourceUserId,sourceUserId);
        }
        if(StringUtils.isNotBlank(vehicleAffiliation)){
            lambda.eq(OilRechargeAccountDetails::getVehicleAffiliation,vehicleAffiliation);
        }
        lambda.eq(OilRechargeAccountDetails::getSourceType,sourceType);
        return this.list(lambda);
    }

    @Override
    public OilRechargeAccountDetails getOilRechargeAccountDetail(Long userId, String pinganAccId, Long sourceUserId, String sourcePinganAccId, Integer sourceType) {
       LambdaQueryWrapper<OilRechargeAccountDetails> lambda= Wrappers.lambdaQuery();
       lambda.eq(OilRechargeAccountDetails::getUserId,userId);
        if((sourceType== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1||sourceType== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3)
                &&StringUtils.isBlank(pinganAccId)){
            throw new BusinessException("现金/授信充值类型不允许虚拟账户为空");
        }
        if(StringUtils.isNotBlank(pinganAccId)){
            lambda.eq(OilRechargeAccountDetails::getPinganAccId,pinganAccId);
        }
        if(StringUtils.isNotBlank(sourcePinganAccId)){
            lambda.eq(OilRechargeAccountDetails::getSourcePinganAccId,sourcePinganAccId);
        }else{
            lambda.isNull(OilRechargeAccountDetails::getSourcePinganAccId);
        }
        if(sourceUserId!=null&&sourceUserId>0L){
            lambda.eq(OilRechargeAccountDetails::getSourceUserId,sourceUserId);
        }
        lambda.eq(OilRechargeAccountDetails::getSourceType,sourceType);
        return this.getOne(lambda);
    }

    /**
     * niejeiwei
     * 预存资金明细-小程序接口
     * 50058
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<OilRechargeAccountDetailsDto> getLockBalanceDetailsOrder(OilRechargeAccountDetailsVo vo, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<OilRechargeAccountDetailsDto> page = new Page<>(pageNum, pageSize);
        vo.setTenantId(loginInfo.getTenantId());
        Page<OilRechargeAccountDetailsDto> orderlimits = baseMapper.getLockBalanceDetailsOrder(page, vo);
        if (null != orderlimits && orderlimits.getRecords().size() > 0) {
            for (OilRechargeAccountDetailsDto dto : orderlimits.getRecords()) {
                Long orderId = dto.getOrderId();
                //查询订单收款人 为空的话查询 历史
                List<OilRechargeAccountDetailsDto> dtos = baseMapper.queryOrderPayee(orderId);
                OilRechargeAccountDetailsDto dto1 = null;
                if (dtos != null && dtos.size() > 0) {
                    dto1 = dtos.get(0);
                } else {
                    List<OilRechargeAccountDetailsDto> dtos1 = baseMapper.queryOrderPayeeH(orderId);
                    if (dtos1 != null && dtos1.size() > 0) {
                        dto1 = dtos1.get(0);
                    }
                }
                if (null != dto1) {
                    dto.setPayeeUserid(dto1.getPayeeUserid());
                    // 字符串拼接
                    dto.setPayeeInfo(dto1.getPayee() + dto.getPayeePhone());
                }
                OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
                if (null == orderScheduler) {
                    OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);
                    if (null != orderSchedulerH) {
                        dto.setPlateNumber(orderSchedulerH.getPlateNumber());
                    }
                } else {
                    dto.setPlateNumber(orderScheduler.getPlateNumber());
                }
                dto.setBanlance(CommonUtil.getDoubleFormatLongMoney(dto.getNoPayOil(), 2));
            }
        }

        return orderlimits;
    }

    @Override
    public List<OilRechargeAccountDetailsDto> queryOrderPayee(String accessToken, Long orderId) {
        List<OilRechargeAccountDetailsDto> list =  new ArrayList<>();
        //查询订单收款人 为空的话查询 历史
        List<OilRechargeAccountDetailsDto> dtos = baseMapper.queryOrderPayee(orderId);
        OilRechargeAccountDetailsDto dto1 = null;
        if (dtos != null && dtos.size() > 0) {
            dto1 = dtos.get(0);
        } else {
            List<OilRechargeAccountDetailsDto> dtos1 = baseMapper.queryOrderPayeeH(orderId);
            if (dtos1 != null && dtos1.size() > 0) {
                dto1 = dtos1.get(0);
            }
        }
        list.add(dto1);
        return list;
    }
}
