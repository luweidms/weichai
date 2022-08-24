package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.SysStsticDataDto;
import com.youming.youche.system.provider.mapper.SysStaticDataMapper;
import com.youming.youche.system.provider.utis.SysStaticDataRedisUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 静态数据表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-11
 */
@DubboService(version = "1.0.0")
@Service
public class SysStaticDataServiceImpl extends BaseServiceImpl<SysStaticDataMapper, SysStaticData>
        implements ISysStaticDataService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    IUserDataInfoService userDataInfoService;


    @Override
    public List<SysStaticData> selectAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<SysStaticData> selectAllByState(Integer state) {
        LambdaQueryWrapper<SysStaticData> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysStaticData::getState, state);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysStaticData> get(String codeType) {
        return (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
    }

    @Override
    public List<List<SysStaticData>> get(String[] codeTypes) {
        List<List<SysStaticData>> lists = Lists.newArrayList();
        for (String codeType : codeTypes) {
            lists.add(get(codeType));
        }
        return lists;
    }

    @Override
    public String getSysStaticDatas(String codeType, Integer codeValue) {
        LambdaQueryWrapper<SysStaticData> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysStaticData::getCodeType, codeType).eq(SysStaticData::getCodeValue, codeValue).last("limit 1");
        SysStaticData sysStaticData = getOne(wrapper);
        if (sysStaticData != null) {
            return sysStaticData.getCodeName();
        }
        return "";
    }

    @Override
    public String getSysStaticDataId(String codeType, String codeValue) {
        if (codeValue == null) {
            codeValue = "0";
        }
        LambdaQueryWrapper<SysStaticData> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysStaticData::getCodeType, codeType).eq(SysStaticData::getId, codeValue).last("limit 1");
        SysStaticData sysStaticData = getOne(wrapper);
        if (sysStaticData != null) {
            return sysStaticData.getCodeName();
        }
        return "";
    }

    @Override
    public List<SysStsticDataDto> getSysStaticDataInfo(List<String> codeType, Long tenantId) {
        List<SysStsticDataDto> listOut = new ArrayList<>();
        for (String ct : codeType) {
            List<SysStaticData> listDatas = SysStaticDataRedisUtils.getSysStaticDataList(redisUtil, tenantId, ct);
            List<SysStsticDataDto> list = new ArrayList<>();
            SysStsticDataDto sysStsticDataDto = new SysStsticDataDto();
            if (listDatas != null) {
                for (SysStaticData ssd : listDatas) {
                    SysStsticDataDto dto = new SysStsticDataDto();
                    dto.setCodeDesc(ssd.getCodeDesc());
                    dto.setCodeName(ssd.getCodeName());
                    dto.setCodeValue(ssd.getCodeValue());
                    dto.setSortId(ssd.getSortId());
                    if (ssd.getCodeDesc() != null) {
                        dto.setCodeDesc(ssd.getCodeDesc());
                    } else {
                        dto.setCodeDesc("");
                    }
                    list.add(dto);
                }
            } else {
                if (codeType.size() == 1) {
                    throw new BusinessException("没有找到字段为[" + ct + "]静态参数数据配置！");
                } else {
                    log.error("获取字段为[" + ct + "]静态参数数据失败，或该静态参数配置不存在。");
                }
            }
            sysStsticDataDto.setCodeType(ct);
            sysStsticDataDto.setListData(list);
            listOut.add(sysStsticDataDto);
        }
        return listOut;
    }

    @Override
    public List<SysStaticData> getSysStaticDataList(String codeType, Long userId) {
        if (StringUtils.isNotEmpty(codeType)) {
            long tenantId = -1L;
            UserDataInfo user = userDataInfoService.getById(userId);
            if (user != null && user.getTenantId() != null) {
                tenantId = user.getTenantId();
            }
            List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
            List<SysStaticData> staticDataList = new ArrayList<>();
            for (SysStaticData sysStaticDate : list
            ) {
                if (sysStaticDate.getTenantId().equals(tenantId)) {
                    staticDataList.add(sysStaticDate);
                }
            }
            return staticDataList;
        } else {
            return null;
        }
    }

    @Override
    public SysStaticData getSysStaticData(String codeType, String codeValue) {
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

    @Override
    public List<SysStaticData> getSysStaticDataListByCodeName(String vehicleLength) {
        List<SysStaticData> sysStaticDataCodeName = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(vehicleLength));
        return sysStaticDataCodeName;
    }
}
