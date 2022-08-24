package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.ISubjectsInfoService;
import com.youming.youche.order.domain.order.SubjectsInfo;
import com.youming.youche.order.provider.mapper.order.SubjectsInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class SubjectsInfoServiceImpl extends BaseServiceImpl<SubjectsInfoMapper, SubjectsInfo> implements ISubjectsInfoService {


    @Override
    public Map<Long, SubjectsInfo> getSubjectsInfo(Long[] subjectsIds, Long tenantId) {
        LambdaQueryWrapper<SubjectsInfo> lambda= Wrappers.lambdaQuery();
        lambda.in(SubjectsInfo::getId,subjectsIds);
        List<SubjectsInfo> list = this.list(lambda);
        Map<Long, SubjectsInfo> reqMap =  new HashMap<Long, SubjectsInfo>();
        for(int i=0;i<list.size();i++){
            SubjectsInfo subjectsInfo = list.get(i);
            reqMap.put(subjectsInfo.getId(), subjectsInfo);
        }
        return reqMap;
    }

    @Override
    public SubjectsInfo getSubjectsInfo(Long subjectsId, Long tenantId) {
        LambdaQueryWrapper<SubjectsInfo> lambda=Wrappers.lambdaQuery();
        lambda.eq(SubjectsInfo::getId,subjectsId);
        return this.getOne(lambda);
    }

    @Override
    public String getSubjectName(Long sujectId) {
        SubjectsInfo subjectsInfo = this.getById(sujectId);
        if(subjectsInfo == null){
            throw new BusinessException("科目id："+sujectId+"科目信息不存在！");
        }
        return subjectsInfo.getSubjectsName();
    }
}
