package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.ICmCustomerInfoService;
import com.youming.youche.order.domain.order.CmCustomerInfo;
import com.youming.youche.order.dto.CmCustomerInfoOutDto;
import com.youming.youche.order.dto.CustomerDto;
import com.youming.youche.order.provider.mapper.order.CmCustomerInfoMapper;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.record.api.cm.ICmCustomerInfoVerService;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.domain.cm.CmCustomerInfoVer;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.STATE.STATE_YES;


/**
 * <p>
 * 客户信息表/客户档案表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
@DubboService(version = "1.0.0")
@Service
public class CmCustomerInfoServiceImpl extends BaseServiceImpl<CmCustomerInfoMapper, CmCustomerInfo> implements ICmCustomerInfoService {
    @DubboReference(version = "1.0.0")
    public ICmCustomerInfoVerService cmCustomerInfoVerService;

    @DubboReference(version = "1.0.0")
    private ISysRoleService sysRoleService;

    @DubboReference(version = "1.0.0")
    private ICmCustomerLineService cmCustomerLineService;

    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    com.youming.youche.record.api.cm.ICmCustomerInfoService iCmCustomerInfoService;

    @Override
    public Long doSaveTmpOrderCustLineInfo(CustomerDto inParam, LoginInfo user) {
        String companyName = inParam.getCustomerName();
        Long customerId= inParam.getCustomerId();
        CmCustomerInfo cmCustomerInfo = new CmCustomerInfo();
        if(customerId < 0){
            boolean flg = this.existsCompanyName(companyName,user.getTenantId(),null);
            if(flg){
                throw new BusinessException("该客户全称已经存在，请选择存在的此客户！");
            }
        }else{
            boolean flg = this.existsCompanyName(companyName,user.getTenantId(),customerId);
            if(flg){
                throw new BusinessException("该客户全称已经存在，请选择存在的此客户！");
            }
            cmCustomerInfo = this.getById(customerId);
            if(null == cmCustomerInfo){
                cmCustomerInfo = new CmCustomerInfo();
            }else if(cmCustomerInfo.getType()!=null&&cmCustomerInfo.getType().intValue()==EnumConsts.CUSTOMER_TYPE.YES){
                throw new BusinessException("该客户不能在此修改，请去客户档案修改！");
            }
        }
        String customerName =inParam.getCustomerName();
        String lineName =inParam.getLineName();
        String lineTel = inParam.getLineTel();
        String address =inParam.getAddress();
        String yongyouCode =inParam.getYongyouCode();
        String lookupName =inParam.getLookupName();

        Long reciveProvinceId =inParam.getReciveProvinceId();
        Long reciveCityId =inParam.getReciveCityId();
        String reciveAddress =inParam.getReciveAddress();
        if(StringUtils.isBlank(companyName)){
            companyName = "";
        }
        if(StringUtils.isBlank(customerName)){
            customerName = "";
        }
        if(StringUtils.isBlank(lookupName)){
            lookupName = "";
        }
        cmCustomerInfo.setReciveProvinceId(reciveProvinceId);
        cmCustomerInfo.setReciveCityId(reciveCityId);
        cmCustomerInfo.setReciveAddress(reciveAddress);
        cmCustomerInfo.setCompanyName(companyName);
        cmCustomerInfo.setCustomerName(customerName);
        cmCustomerInfo.setLineName(lineName);
        cmCustomerInfo.setLineTel(lineTel);
        cmCustomerInfo.setLinePhone(lineTel);
        cmCustomerInfo.setAddress(address);
        cmCustomerInfo.setYongyouCode(yongyouCode);
        cmCustomerInfo.setLookupName(lookupName);
        cmCustomerInfo.setType(EnumConsts.CUSTOMER_TYPE.NO);
        cmCustomerInfo.setState(STATE_YES);
        cmCustomerInfo.setCreateTime(LocalDateTime.now());
        cmCustomerInfo.setTenantId(user.getTenantId());
        this.saveOrUpdate(cmCustomerInfo);
        CmCustomerInfoVer cmCustomerInfoVer = new CmCustomerInfoVer();
        BeanUtils.copyProperties(cmCustomerInfo,cmCustomerInfoVer);
       /* cmCustomerInfoVer.setCustomerId(cmCustomerInfo.getCustomerId());
        cmCustomerInfoVer.setCompanyName(companyName);
        cmCustomerInfoVer.setCustomerName(customerName);
        cmCustomerInfoVer.setLineName(lineName);
        cmCustomerInfoVer.setLineTel(lineTel);
        cmCustomerInfo.setLinePhone(lineTel);
        cmCustomerInfoVer.setAddress(address);
        cmCustomerInfoVer.setYongyouCode(yongyouCode);
        cmCustomerInfoVer.setLookupName(lookupName);*/
//        cmCustomerInfoVer.setType(EnumConsts.CUSTOMER_TYPE.NO);
//        cmCustomerInfoVer.setTenantId(user.getTenantId());
        cmCustomerInfoVerService.saveOrUpdate(cmCustomerInfoVer);
        return cmCustomerInfo.getId();
    }

    @Override
    public CmCustomerInfo queryCustomerForOrderTask(long customerId, List<Long> orgList, LoginInfo user) {
         if (orgList == null) {
            throw new BusinessException("归属部门不能为空");
        }

        if (customerId == 0) {
            log.error("客户编号为空");
            return null;
        }

        //判断是否有所有数据权限
        Boolean hasAllDataPermission = sysRoleService.hasAllData(user);
        boolean hasPermisson = true;

        if (!hasAllDataPermission) {

            LambdaQueryWrapper<CmCustomerLine> lambdaQueryWrapper= Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(CmCustomerLine::getCustomerId,customerId);

            if (orgList.size() > 0) {
                lambdaQueryWrapper.in(CmCustomerLine::getSaleDaparment,orgList);
            }
            if (cmCustomerLineService.list(lambdaQueryWrapper) == null || cmCustomerLineService.list(lambdaQueryWrapper) .size() <= 0) {
                hasPermisson = false;//无所有数据权限，且没有该客户的线路权限则不能查询该客户的信息
            }
        }

        if (hasPermisson) {
            LambdaQueryWrapper<CmCustomerInfo> lambda=Wrappers.lambdaQuery();
            lambda.eq(CmCustomerInfo::getId,customerId);
            return this.getOne(lambda);
        }

        return null;
    }

    public boolean existsCompanyName(String companyName, Long tenantId,Long customerId){
        LambdaQueryWrapper<CmCustomerInfo> lambda=new QueryWrapper<CmCustomerInfo>().lambda();
        if(null != customerId){
            lambda.ne(CmCustomerInfo::getId,customerId);
        }
        lambda.eq(CmCustomerInfo::getCompanyName,companyName);
        lambda.eq(CmCustomerInfo::getTenantId,tenantId);
        return this.count(lambda) >0;
    }


    @Override
    public Object getCustomerInfo(Long customerId,Integer isEdit, String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Object obj ;
        //修改 isEdit 0为新增 1为修改 2为审核3为查看
        if(isEdit == 2 || isEdit == 3) {
            Object ver = this.getById(customerId);

            //审核时增加判断是否有部门编号
            CmCustomerInfoVer cust = (CmCustomerInfoVer) ver;
            if (cust.getOrgId() == null || cust.getOrgId() <= 0) {
                throw new BusinessException("客户部门为空，请先设置部门");
            }
            obj = this.getById(customerId);
            Map outVerMap = null;
            try {
                outVerMap = com.youming.youche.order.util.BeanUtils.convertBean2Map(ver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int oddWay = DataFormat.getIntKey(outVerMap,"oddWay");
            int state = DataFormat.getIntKey(outVerMap,"state");
            int  payWay = DataFormat.getIntKey(outVerMap,"payWay");
            long orgIdVer = DataFormat.getLongKey(outVerMap,"orgId");
            long reciveProvinceId = DataFormat.getLongKey(outVerMap,"reciveProvinceId");
            long reciveCityId = DataFormat.getLongKey(outVerMap,"reciveCityId");
            if(oddWay > -1){
                String oddWayName = readisUtil.getSysStaticData("RECIVE_TYPE",oddWay+"").getCodeName();
                outVerMap.put("oddWayName",oddWayName);
            }
            if(payWay > -1){
                String payWayName = readisUtil.getSysStaticData("BALANCE_TYPE",payWay+"").getCodeName();
                outVerMap.put("payWayName",payWayName);
            }
            if(state > -1){
                String stateName = readisUtil.getSysStaticData("SYS_STATE_DESC",state+"").getCodeName();
                outVerMap.put("stateName",stateName);
            }
            if(reciveProvinceId > -1){
                //获取省名称
                String provinceName = "";
                if (reciveProvinceId > 0) {
                    SysStaticData province = readisUtil.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE, reciveProvinceId + "");
                    if (province != null && province.getId() >0) {
                        provinceName =  province.getCodeName();
                    }else{
                        throw new BusinessException("省编号："+reciveProvinceId+"查找失败!");
                    }
                }
                outVerMap.put("reciveProvinceName",provinceName);
            }
            if(reciveCityId > -1){
                //获取市名称
                String cityName = "";
                if (reciveCityId > 0) {
                    SysStaticData city = readisUtil.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_CITY, reciveCityId + "");
                    if (city != null && city.getId()>0) {
                        cityName = city.getCodeName();
                    }else{
                        throw new BusinessException("市编号："+reciveCityId+"查找失败!");
                    }
                }
                outVerMap.put("reciveCityName",cityName);
            }
            if (orgIdVer > -1) {
                outVerMap.put("orgName", sysOrganizeService.getOrgNameByOrgId(orgIdVer,loginInfo.getTenantId()));
            }


            Map outObjMap = null;
            outObjMap = com.youming.youche.order.util.BeanUtils.convertBean2Map(obj);
            int oddWayObj = DataFormat.getIntKey(outObjMap,"oddWay");
            int stateObj = DataFormat.getIntKey(outObjMap,"state");
            int  payWayObj = DataFormat.getIntKey(outObjMap,"payWay");
            long orgId = DataFormat.getLongKey(outObjMap,"orgId");
            if(oddWayObj > -1){
                String oddWayName = readisUtil.getSysStaticData("RECIVE_TYPE",oddWayObj+"").getCodeName();
                outObjMap.put("oddWayName",oddWayName);
            }
            if(payWayObj > -1){
                String payWayName = readisUtil.getSysStaticData("BALANCE_TYPE",payWayObj+"").getCodeName();
                outObjMap.put("payWayName",payWayName);
            }
            if(stateObj > -1){
                String stateName = readisUtil.getSysStaticData("SYS_STATE_DESC",stateObj+"").getCodeName();
                outObjMap.put("stateName",stateName);
            }

            if(reciveProvinceId > -1){
                //获取省名称
                String provinceName = "";
                if (reciveProvinceId > 0) {
                    SysStaticData province = readisUtil.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE, reciveProvinceId + "");
                    if (province != null && province.getId() >0) {
                        provinceName =  province.getCodeName();
                    }else{
                        throw new BusinessException("省编号："+reciveProvinceId+"查找失败!");
                    }
                }
                outObjMap.put("reciveProvinceName",provinceName);
            }
            if(reciveCityId > -1){
                //获取市名称
                String cityName = "";
                if (reciveCityId > 0) {
                    SysStaticData city = readisUtil.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_CITY, reciveCityId + "");
                    if (city != null && city.getId()>0) {
                        cityName = city.getCodeName();
                    }else{
                        throw new BusinessException("市编号："+reciveCityId+"查找失败!");
                    }
                }
                outObjMap.put("reciveCityName",cityName);
            }
            if (orgId > -1) {
                outObjMap.put("orgName", sysOrganizeService.getOrgNameByOrgId(orgId,loginInfo.getTenantId()));
            }
            Map rtnMap = new HashMap();
            rtnMap.put("obj",outObjMap);
            rtnMap.put("ver",outVerMap);
            return rtnMap;

        }else{
            obj = iCmCustomerInfoService.getInfoById(customerId,accessToken);
        }
        CmCustomerInfoOutDto out = new CmCustomerInfoOutDto();
        if(obj != null ){
            BeanUtils.copyProperties(obj,out);
        }
        return out;
    }
}
