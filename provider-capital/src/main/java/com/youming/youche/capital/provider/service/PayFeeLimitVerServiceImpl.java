package com.youming.youche.capital.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.youming.youche.capital.api.IPayFeeLimitService;
import com.youming.youche.capital.domain.PayFeeLimit;
import com.youming.youche.capital.domain.PayFeeLimitVer;
import com.youming.youche.capital.provider.mapper.PayFeeLimitMapper;
import com.youming.youche.capital.provider.mapper.PayFeeLimitVerMapper;
import com.youming.youche.capital.api.IPayFeeLimitVerService;
import com.youming.youche.capital.vo.PayFeeLimitVerVo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-03-03
 */
@DubboService(version = "1.0.0")
public class PayFeeLimitVerServiceImpl extends BaseServiceImpl<PayFeeLimitVerMapper, PayFeeLimitVer> implements IPayFeeLimitVerService {

    @DubboReference(version = "1.0.0")
    ISysStaticDataService sysStaticDataService;

    @Resource
    private PayFeeLimitVerMapper payFeeLimitVerMapper;

    @Resource
    private PayFeeLimitMapper payFeeLimitMapper;

    @Resource
    private IPayFeeLimitService iPayFeeLimitService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Override
    public List<PayFeeLimitVerVo> queryCheckFailMsg() {
        return payFeeLimitVerMapper.queryCheckFailMsg();
    }

    @Override
    public List<PayFeeLimitVerVo> queryPayFeeLimitCfgUpdt(String accessToken, String codeType, String codeDesc) throws InvocationTargetException, IllegalAccessException {

        LoginInfo loginInfo = loginUtils.get(accessToken);

        PayFeeLimitVer qryVer=new PayFeeLimitVer();
        qryVer.setTenantId(loginInfo.getTenantId());
        qryVer.setFlag(0);
        qryVer.setType(Integer.valueOf(codeDesc));
        List<PayFeeLimitVer> limitList =baseMapper.selectByFee(qryVer);

        if (limitList == null || limitList.size()==0) {
            limitList = new ArrayList<>();
        }

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

        //拼装输出数据
        List<PayFeeLimitVerVo> outLists = new ArrayList<PayFeeLimitVerVo>();

        for (SysStaticData data : restLists) {
            boolean notExistCfg = true;
            for (PayFeeLimitVer limit : limitList) {
                if (data.getCodeId() == limit.getSubType().longValue()) {
                    PayFeeLimitVerVo payFeeLimitVo=new PayFeeLimitVerVo();
                    BeanUtils.copyProperties(limit,payFeeLimitVo);
                    outLists.add(payFeeLimitVo);
                    notExistCfg = false;
                }
            }

            if (notExistCfg) {

                PayFeeLimitVer limitVer = new PayFeeLimitVer();
                limitVer.setSubType(Integer.parseInt(data.getCodeId() + ""));
                limitVer.setSubTypeName(data.getCodeName());
                limitVer.setType(Integer.parseInt(codeDesc));
                limitVer.setValue(-1L);

                PayFeeLimitVerVo payFeeLimitVo=new PayFeeLimitVerVo();
                BeanUtils.copyProperties(limitVer,payFeeLimitVo);
                outLists.add(payFeeLimitVo);
            }

        }
        return outLists;
    }

    @Override
    @Transactional
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {

        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + token);

        PayFeeLimitVer qryVer=new PayFeeLimitVer();
        qryVer.setTenantId(busiId);
        qryVer.setFlag(0);

        //根据批次号查出所有的修改记录
        List<PayFeeLimitVer> payFeeLimitVerList = baseMapper.selectByFee(qryVer);

        for (PayFeeLimitVer payFeeLimitVer : payFeeLimitVerList) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tenant_id", loginInfo.getTenantId());
            map.put("type", payFeeLimitVer.getType());
            map.put("sub_type", payFeeLimitVer.getSubType());

            //删除原有记录
            iPayFeeLimitService.removeByMap(map);

            //保存新记录
            PayFeeLimit newPayFeeLimit = new PayFeeLimit();
            BeanUtil.copyProperties(payFeeLimitVer,newPayFeeLimit);
            if(newPayFeeLimit.getCreateDate()==null){
                newPayFeeLimit.setCreateDate(LocalDateTime.now());
            }
            iPayFeeLimitService.save(newPayFeeLimit);

        }


        PayFeeLimitVer payFeeLimitVer = new PayFeeLimitVer();
        payFeeLimitVer.setTenantId(loginInfo.getTenantId());
        payFeeLimitVer.setRemark(desc);
        payFeeLimitVer.setFlag(1);

        //更新ver表里的数据状态为:1-审批通过
        baseMapper.updateByIdAndFlag(payFeeLimitVer);

        saveSysOperLog(SysOperLogConst.BusiCode.CapitalControl, SysOperLogConst.OperType.Audit, "资金风控审核通过", token, loginInfo.getTenantId());



        PayFeeLimitVer payFeeLimitVer2 = new PayFeeLimitVer();
        payFeeLimitVer2.setTenantId(loginInfo.getTenantId());

        //清空审核失败的原因
        baseMapper.updateReRemark(payFeeLimitVer2);
    }

    @Override
    @Transactional
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {

        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + token);

        PayFeeLimitVer payFeeLimitVer = new PayFeeLimitVer();
        payFeeLimitVer.setTenantId(loginInfo.getTenantId());
        payFeeLimitVer.setRemark(desc);
        payFeeLimitVer.setFlag(2);

        //更新ver表里的数据状态为:2-审批不通过
        baseMapper.updateByIdAndFlag(payFeeLimitVer);

        saveSysOperLog(SysOperLogConst.BusiCode.CapitalControl, SysOperLogConst.OperType.Audit, "资金风控审核未通过", token, loginInfo.getTenantId());
    }

    @Override
    @Transactional
    public String saveOrUpdate(String accessToken,List<PayFeeLimitVer> payFeeLimitVerList) throws Exception {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);

        //生成这一次修改的批次编号
        SimpleDateFormat format0 = new SimpleDateFormat("yyMMddHHmmssSSS");
        String batchNo = format0.format(new Date());
        LocalDateTime curDate=LocalDateTime.now();
        for (int i = 0; i < payFeeLimitVerList.size(); i++) {
           // PayFeeLimit
            PayFeeLimitVer payFeeLimitVer=payFeeLimitVerList.get(i);
            payFeeLimitVer.setHisId(payFeeLimitVer.getId());
            payFeeLimitVer.setId(null);
            payFeeLimitVer.setBatchNo(batchNo);
            payFeeLimitVer.setOpDate(curDate);
            payFeeLimitVer.setOpId(loginInfo.getId());
            payFeeLimitVer.setOpName(loginInfo.getName());
            payFeeLimitVer.setFlag(0);
            payFeeLimitVer.setTenantId(loginInfo.getTenantId());
            //更新历史表数据
            payFeeLimitVerMapper.updateByFee(payFeeLimitVer);

            //新增实表数据
            baseMapper.insertPay(payFeeLimitVer);

        }
        saveSysOperLog(SysOperLogConst.BusiCode.CapitalControl, SysOperLogConst.OperType.Update, "提交审核", accessToken, loginInfo.getTenantId());

//        启动审核流程
//        审核流程
//        Map inMap = new HashMap();
//        inMap.put("svName", "iPayFeeLimitVerService");
//       boolean bool = false;
//        try {
//            bool = auditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_CAPITALCONTROL, loginInfo.getTenantId(), SysOperLogConst.BusiCode.CapitalControl, inMap, accessToken);
//            if (!bool) {
//                log.error("启动审核流程失败！");
//                throw new BusinessException("启动审核流程失败！");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sucess(loginInfo.getTenantId(),"",null,accessToken);

        return "成功";
    }

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


    /**
     * 初始化资金风控信息
     * @param tenantId
     * @return true 初始化成功
     */
    @Override
    @Transactional
    public boolean initPayFeeLimit(Long tenantId,String accessToken) throws Exception{


        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        log.debug("----初始化资金风控信息----tenantId----" + tenantId);

        if (tenantId <= 0) {
            throw new BusinessException("租户ID错误");
        }


        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tenant_id", tenantId);

        List<PayFeeLimit> payFeeList = payFeeLimitMapper.selectByMap(paramMap);

        if (payFeeList != null && payFeeList.size() > 0) {
            throw new BusinessException("该租户已经存在资金风控配置");
        }

        List<SysStaticData> sysStaticDataList =sysStaticDataService.get("PAY_FEE_LIMIT_SUB_TYPE");

        for (SysStaticData sysStaticData : sysStaticDataList) {

            PayFeeLimit payFeeLimit = new PayFeeLimit();

            payFeeLimit.setType(Integer.parseInt(sysStaticData.getCodeDesc()));
            payFeeLimit.setSubType(Integer.parseInt(sysStaticData.getCodeId() + ""));
            payFeeLimit.setSubTypeName(sysStaticData.getCodeName());
            payFeeLimit.setValue(Long.parseLong(sysStaticData.getCodeValue()));
            payFeeLimit.setCreateDate(LocalDateTime.now());
            payFeeLimit.setTenantId(tenantId);
            payFeeLimit.setOpId(loginInfo.getId());
            payFeeLimit.setOpName(loginInfo.getName());
            payFeeLimit.setOpDate(LocalDateTime.now());
            iPayFeeLimitService.save(payFeeLimit);
        }

        return true;
    }

}
