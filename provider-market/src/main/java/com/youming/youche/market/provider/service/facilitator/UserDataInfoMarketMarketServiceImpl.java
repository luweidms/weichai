package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.ITenantStaffRelService;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.market.api.facilitator.IUserDataInfoVerService;
import com.youming.youche.market.domain.facilitator.TenantStaffRel;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.domain.facilitator.UserDataInfoVer;
import com.youming.youche.market.dto.facilitator.UserDataInfoInDto;
import com.youming.youche.market.provider.mapper.facilitator.UserDataInfoMarketMapper;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 用户资料信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
@Service
public class UserDataInfoMarketMarketServiceImpl extends BaseServiceImpl<UserDataInfoMarketMapper, UserDataInfo> implements IUserDataInfoMarketService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    private UserDataInfoMarketMapper userDataInfoMapper;

    @Resource
    private IUserDataInfoVerService userDataInfoVerService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Resource
    private ITenantStaffRelService tenantStaffRelService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;



    /**
     * 实现功能: 通过accessToken获取登录用户信息
     * @param
     * @return
     *
     */
    @Override
    public UserDataInfo getUserDataInfoByAccessToken(String accessToken) {
        UserDataInfo userDataInfo = null;
        userDataInfo = (UserDataInfo) redisUtil.get("user_info:" + accessToken);
        return userDataInfo;
    }



    @Override
    public UserDataInfo getUserDataInfoByLoginAcct(String loginAcct) {
        UserDataInfo userDataInfo=null;
        Map<String,Object> userMap = getSysOperatorInfoByLoginAcct(loginAcct);
        if(DataFormat.getIntKey(userMap,"info") == 1){
            userDataInfo = userDataInfoMapper.selectById(DataFormat.getLongKey(userMap, "userId"));

        }
        return userDataInfo;
    }

    public Map getSysOperatorInfoByLoginAcct(String loginAcct){
        Map resultMap = new HashMap();
        resultMap.put("info",0);
        List<UserDataInfo> list = userDataInfoMapper.getUserDataInfoByLoginAcct(loginAcct);
        LambdaQueryWrapper<UserDataInfo> lambda=new QueryWrapper<UserDataInfo>().lambda();
        lambda.eq(UserDataInfo::getMobilePhone,loginAcct);
        UserDataInfo dataInfo = userDataInfoMapper.selectOne(lambda);
        if(dataInfo==null &&  list==null && list.isEmpty()){
            return resultMap;
        }
        resultMap.put("info",1);
        resultMap.put("loginAcct",loginAcct);
        if(list != null && list.size() >0){
            UserDataInfo userDataInfo = list.get(0);
            if(userDataInfo != null){

                if(null != userDataInfo.getLinkman()) {
                    resultMap.put("linkman", userDataInfo.getLinkman());
                }
                if(null != userDataInfo.getUserPrice()){
                    resultMap.put("userPrice",userDataInfo.getUserPrice());
                }
                if(null != userDataInfo.getUserPriceUrl()){
                    resultMap.put("userPriceUrl",userDataInfo.getUserPriceUrl());
                }
                if(null != userDataInfo.getId()){
                    resultMap.put("userId",userDataInfo.getId());
                }
            }
        }
        if(dataInfo != null){
            if(null != dataInfo.getLinkman()) {
                resultMap.put("linkman",dataInfo.getLinkman());
            }
            if(null != dataInfo.getUserPrice()){
                resultMap.put("userPrice",dataInfo.getUserPrice());
            }
            if(null != dataInfo.getUserPriceUrl()){
                resultMap.put("userPriceUrl",dataInfo.getUserPriceUrl());
            }
            if(null != dataInfo.getId()){
                resultMap.put("userId",dataInfo.getId());
            }
        }


        return resultMap;
    }

    @Override
    public Long saveUserDataInfo(UserDataInfo userDataInfo) {
         this.save(userDataInfo);
        return userDataInfo.getId();
    }

    @Override
    public void modifyUserDataInfo(UserDataInfoInDto in, LoginInfo user) {
        if(in.getUserId()==null||in.getUserId()<=0){
            throw new BusinessException("userId不能为空！");
        }
        UserDataInfo userDataInfo = this.get(in.getUserId());
        if(userDataInfo==null){
            throw new BusinessException("用户信息不存在！");
        }

        BeanUtils.copyProperties(in,userDataInfo);
        this.update(userDataInfo);

        modifyUserName(in.getUserId(), in.getLinkman(),user);

        UserDataInfoVer ver = new UserDataInfoVer();
        BeanUtils.copyProperties(userDataInfo,ver);
        ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
        userDataInfoVerService.save(ver);
    }

    @Override
    public String doUpdateServ(UserDataInfoInDto userDataInfoIn) {
        if(StringUtils.isEmpty(userDataInfoIn.getLinkman())){
            throw new BusinessException("联系人不能为空！");
        }
        if(userDataInfoIn.getUserId()==null||userDataInfoIn.getUserId()<=0){
            throw new BusinessException("用户编号不能为空！");

        }
        UserDataInfoVer ver = userDataInfoVerService.getUserDataInfoVerNoTenant(userDataInfoIn.getUserId());
        if(ver!=null) {
            ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
            userDataInfoVerService.save(ver);
        }


        //先查出来原表
        UserDataInfo userDataInfo = this.getById(userDataInfoIn.getUserId());//用户基本信息
        if(userDataInfo==null){
            throw new BusinessException("服务商不存在");
        }

        //复制到ver表
        //ver表
        UserDataInfoVer userDataInfoVer = new UserDataInfoVer();
        BeanUtils.copyProperties(userDataInfo,userDataInfoVer);
        userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        BeanUtils.copyProperties(userDataInfoIn,userDataInfoVer);
        userDataInfoVerService.save(userDataInfoVer);
        userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
        this.update(userDataInfo);


        SysUser operator = sysUserService.getByUserInfoId(userDataInfoIn.getUserId());
        operator.setOpName(userDataInfoIn.getLinkman());
        sysUserService.saveOrUpdate(operator);

        return "Y";
    }

    @Override
    public String doAuditServ(Long userId, Boolean auditFlg, String auditDesc,LoginInfo baseUser) {
        if(userId<=0){
            throw new BusinessException("用户编号不能为空！");

        }
        if(auditFlg) {
            UserDataInfoVer userDataInfoVer = userDataInfoVerService.getUserDataInfoVerNoTenant(userId);
            UserDataInfo userDataInfo = this.getById(userId);
            if (userDataInfoVer != null) {
                Long id = userDataInfo.getId();
                BeanUtils.copyProperties(userDataInfoVer,userDataInfo);
                userDataInfo.setId(id);
                userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
                userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
                userDataInfo.setVerifReason("");
                userDataInfo.setAuthManId(baseUser.getId());
                this.update(userDataInfo);
                LambdaUpdateWrapper<UserDataInfo> updateWrapper=new UpdateWrapper<UserDataInfo>().lambda();
                updateWrapper.set(UserDataInfo::getUserPrice,userDataInfo.getUserPrice())
                              .set(UserDataInfo::getUserPriceUrl,userDataInfo.getUserPriceUrl())
                              .set(UserDataInfo::getIdenPictureBack,userDataInfo.getIdenPictureBack())
                              .set(UserDataInfo::getIdenPictureBackUrl,userDataInfo.getIdenPictureBackUrl())
                               .set(UserDataInfo::getIdenPictureFront,userDataInfo.getIdenPictureFront())
                               .set(UserDataInfo::getIdenPictureFrontUrl,userDataInfo.getIdenPictureFrontUrl())
                              .eq(UserDataInfo::getId,userDataInfo.getId());
                this.update(updateWrapper);

                userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                userDataInfoVerService.saveOrUpdate(userDataInfoVer);
            }


            List<TenantStaffRel> list = tenantStaffRelService.getTenantStaffRelByUserId(userId);
            for (TenantStaffRel rel : list) {
                rel.setStaffName(userDataInfo.getLinkman());
            }

            SysTenantDef tenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
            if (null != tenantDef) {
                tenantDef.setLinkMan(userDataInfo.getLinkman());
                sysTenantDefService.update(tenantDef);
            }
        }else{
            //写日志
            UserDataInfoVer userDataInfoVer = userDataInfoVerService.getUserDataInfoVerNoTenant(userId);
            UserDataInfo userDataInfo = this.getById(userId);
            if (userDataInfoVer != null) {
                userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
                userDataInfo.setVerifReason(auditDesc);
                userDataInfo.setAuthManId(baseUser.getId());
                this.saveOrUpdate(userDataInfo);
                userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                userDataInfoVerService.save(userDataInfoVer);
            }
        }

        return "Y";
    }


    public void modifyUserName(Long userId, String linkman, LoginInfo user) {
        List<TenantStaffRel> list = tenantStaffRelService.getTenantStaffRel(userId, user, false);
        for (TenantStaffRel rel : list) {
            rel.setStaffName(linkman);
        }
        SysTenantDef tenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId, true);
        if (null != tenantDef) {
            tenantDef.setLinkMan(linkman);
            sysTenantDefService.update(tenantDef);
        }
        SysUser sysUser = sysUserService.getByUserInfoId(userId);
        sysUser.setOpName(linkman);
        sysUserService.updateById(sysUser);
    }
}
