package com.youming.youche.capital.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.capital.api.IPayFeeLimitService;
import com.youming.youche.capital.domain.PayFeeLimit;
import com.youming.youche.capital.provider.mapper.PayFeeLimitMapper;
import com.youming.youche.capital.vo.PayFeeLimitVo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.record.api.order.IOrderInfoServie;
import com.youming.youche.system.api.ISysStaticDataService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author Terry
* @since 2022-03-02
*/
@DubboService(version = "1.0.0")
public class PayFeeLimitServiceImpl extends BaseServiceImpl<PayFeeLimitMapper, PayFeeLimit> implements IPayFeeLimitService {


    @DubboReference(version = "1.0.0")
    ISysStaticDataService sysStaticDataService;

    @Resource
    RedisUtil redisUtil;

    @Override
    public List<PayFeeLimitVo> queryPayFeeLimitCfg(String accessToken,String codeType, String codeDesc) throws InvocationTargetException, IllegalAccessException {

        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        LambdaQueryWrapper<PayFeeLimit> wrapper= Wrappers.lambdaQuery();
        wrapper.eq(PayFeeLimit::getTenantId,loginInfo.getTenantId());
        List<PayFeeLimit> limitList =baseMapper.selectList(wrapper);

        List<SysStaticData> lists =sysStaticDataService.get(codeType);


        if (lists == null) {
            lists = new ArrayList<>();
        }

        List<SysStaticData> restLists = new ArrayList();

        for (SysStaticData data : lists) {
            if (data.getCodeDesc() != null && data.getCodeDesc().equals(codeDesc)) {
                restLists.add(data);
            }
        }
        List<PayFeeLimitVo> outLists = new ArrayList<PayFeeLimitVo>();
        for (SysStaticData data : restLists) {
            boolean notExistCfg = true;
            for (PayFeeLimit limit : limitList) {
                if (data.getCodeId() == limit.getSubType().longValue()) {
                    PayFeeLimitVo payFeeLimitVo=new PayFeeLimitVo();
                    //BeanUtils.copyProperties(payFeeLimitVo,limit);
                    BeanUtils.copyProperties(limit,payFeeLimitVo);
                    payFeeLimitVo.setUnitName(data.getCodeTypeAlias());
                    outLists.add(payFeeLimitVo);
                    notExistCfg = false;
                }
            }

            if (notExistCfg) {

                PayFeeLimit limit = new PayFeeLimit();
                limit.setSubType(Integer.parseInt(data.getCodeId() + ""));
                limit.setSubTypeName(data.getCodeName());
                limit.setType(Integer.parseInt(codeDesc));
                limit.setValue(0L);

                PayFeeLimitVo payFeeLimitVo=new PayFeeLimitVo();
                BeanUtils.copyProperties(limit,payFeeLimitVo);
                payFeeLimitVo.setUnitName(data.getCodeTypeAlias());//单位名称
                outLists.add(payFeeLimitVo);
            }

        }
        return outLists;
    }

    @Override
    public List<PayFeeLimitVo> queryPayFeeLimitCfgOrder(String codeType, String codeDesc, Long tenantId) {
        //根据订单号查车队id查询

        LambdaQueryWrapper<PayFeeLimit> wrapper= Wrappers.lambdaQuery();
        wrapper.eq(PayFeeLimit::getTenantId,tenantId);
        List<PayFeeLimit> limitList =baseMapper.selectList(wrapper);

        List<SysStaticData> lists =sysStaticDataService.get(codeType);


        if (lists == null) {
            lists = new ArrayList<>();
        }

        List<SysStaticData> restLists = new ArrayList();

        for (SysStaticData data : lists) {
            if (data.getCodeDesc() != null && data.getCodeDesc().equals(codeDesc)) {
                restLists.add(data);
            }
        }
        List<PayFeeLimitVo> outLists = new ArrayList<PayFeeLimitVo>();
        for (SysStaticData data : restLists) {
            boolean notExistCfg = true;
            for (PayFeeLimit limit : limitList) {
                if (data.getCodeId() == limit.getSubType().longValue()) {
                    PayFeeLimitVo payFeeLimitVo=new PayFeeLimitVo();
                    //BeanUtils.copyProperties(payFeeLimitVo,limit);
                    BeanUtils.copyProperties(limit,payFeeLimitVo);
                    payFeeLimitVo.setUnitName(data.getCodeTypeAlias());
                    outLists.add(payFeeLimitVo);
                    notExistCfg = false;
                }
            }

            if (notExistCfg) {

                PayFeeLimit limit = new PayFeeLimit();
                limit.setSubType(Integer.parseInt(data.getCodeId() + ""));
                limit.setSubTypeName(data.getCodeName());
                limit.setType(Integer.parseInt(codeDesc));
                limit.setValue(0L);

                PayFeeLimitVo payFeeLimitVo=new PayFeeLimitVo();
                BeanUtils.copyProperties(limit,payFeeLimitVo);
                payFeeLimitVo.setUnitName(data.getCodeTypeAlias());//单位名称
                outLists.add(payFeeLimitVo);
            }

        }
        return outLists;
    }
}
