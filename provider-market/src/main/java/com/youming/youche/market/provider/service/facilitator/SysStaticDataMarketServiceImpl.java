package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.ISysStaticDataMarketService;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.provider.mapper.facilitator.SysStaticDataMapper;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 静态数据表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
@DubboService(version = "1.0.0")
@Service
public class SysStaticDataMarketServiceImpl extends BaseServiceImpl<SysStaticDataMapper, SysStaticData> implements ISysStaticDataMarketService {
    @Resource
    private SysStaticDataMapper sysStaticDataMapper;

    @Resource
    private IUserDataInfoMarketService iUserDataInfoMarketService;

    @Resource
    RedisUtil redisUtil;


    @Override
    public List<SysStaticData> getStaticData(String codeType, String accessToken) throws Exception {
        if (StringUtils.isEmpty(codeType)) {
            throw new BusinessException("参数错误");
        }
        UserDataInfo user = iUserDataInfoMarketService.getUserDataInfoByAccessToken(accessToken);
        Long tenantId = user.getTenantId();
        QueryWrapper<SysStaticData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CODE_TYPE", codeType);
        queryWrapper.eq("TENANT_ID", tenantId);
        queryWrapper.eq("STATE", 1);
        List<SysStaticData> list = sysStaticDataMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list;
        } else {
            QueryWrapper<SysStaticData> qw = new QueryWrapper<>();
            qw.eq("CODE_TYPE", codeType);
            qw.eq("TENANT_ID", -1);
            qw.eq("STATE", 1);
            return sysStaticDataMapper.selectList(qw);
        }
    }

    @Override
    public SysStaticData getSysStaticDataCodeName(String codeType, Integer codeValue) throws Exception {
        QueryWrapper<SysStaticData> sysStaticDataQueryWrapper = new QueryWrapper<>();
        System.out.println("codeType:" + codeType + "codeValue:" + codeValue);
        sysStaticDataQueryWrapper.eq("CODE_TYPE", codeType).eq("CODE_VALUE", codeValue);
        List<SysStaticData> list = sysStaticDataMapper.selectList(sysStaticDataQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new SysStaticData();
    }

    @Override
    public SysStaticData getSysStaticDataCodeName(String codeType, String codeValue) throws Exception {
        QueryWrapper<SysStaticData> sysStaticDataQueryWrapper = new QueryWrapper<>();
        sysStaticDataQueryWrapper.eq("CODE_TYPE", codeType).eq("CODE_VALUE", codeValue);
        List<SysStaticData> list = sysStaticDataMapper.selectList(sysStaticDataQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new SysStaticData();
    }

    @Override
    public List<SysStaticData> getSysStaticDataList(String codeType, String accessToken) {
        if (StringUtils.isNotEmpty(codeType)) {
            long tenantId = -1L;
            UserDataInfo user = iUserDataInfoMarketService.getUserDataInfoByAccessToken(accessToken);
            if (user != null && user.getTenantId() != null) {
                tenantId = user.getTenantId();
            }
            return getSysStaticDataList(tenantId, codeType);
        } else {
            return null;
        }
    }

    @Override
    public SysStaticData getSysStaticData(String codeType, String codeValue, String accessToken) throws Exception {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }


    public List<SysStaticData> getSysStaticDataList(long tenantId, String codeType) {
        List<SysStaticData> staticDataList = null;
        if (StringUtils.isNotEmpty(codeType)) {
            QueryWrapper<SysStaticData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE_TYPE", codeType);
            queryWrapper.eq("TENANT_ID", tenantId);
            queryWrapper.eq("STATE", 1);
            staticDataList = sysStaticDataMapper.selectList(queryWrapper);
        }
        if (staticDataList == null || staticDataList.isEmpty()) {
            QueryWrapper<SysStaticData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CODE_TYPE", codeType);
            queryWrapper.eq("TENANT_ID", tenantId);
//          staticDataList = (List)CacheFactory.get(SysStaticDataCache.class, codeType);
            staticDataList = sysStaticDataMapper.selectList(queryWrapper);
        }

        return staticDataList;
    }

    @Override
    public String getSysStaticDatas(String codeType, Integer codeValue) {

        SysStaticData sysStaticData = sysStaticDataMapper.getSysStaticDataCodeName(codeType, codeValue);
        ;
        if (sysStaticData != null) {
            return sysStaticData.getCodeName();
        } else {
            return null;
        }
    }

    @Override
    public List<SysStaticData> getSysStaticDataListByCodeName(String facilitatorLength) {
        List<SysStaticData> sysStaticDataCodeName = sysStaticDataMapper.getSysStaticDataListByCodeName(facilitatorLength);
        return sysStaticDataCodeName;
    }

    @Override
    public SysStaticData getSysSyaticDataByCodeNameAndCodeType(String codeType, String codeName) {
        LambdaQueryWrapper<SysStaticData> lambda=new QueryWrapper<SysStaticData>().lambda();
        lambda.eq(SysStaticData::getCodeType,codeType)
                .eq(SysStaticData::getCodeName,codeName);
        return this.getOne(lambda);
    }

}
