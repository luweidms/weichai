package com.youming.youche.system.provider.service.audit;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.system.api.audit.IAuditUserService;
import com.youming.youche.system.api.audit.IAuditUserVerService;
import com.youming.youche.system.domain.audit.AuditUser;
import com.youming.youche.system.domain.audit.AuditUserVer;
import com.youming.youche.system.provider.mapper.audit.AuditUserMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 节点审核人 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AuditUserServiceImpl extends ServiceImpl<AuditUserMapper, AuditUser>
        implements IAuditUserService {


    @Resource
    private  IAuditUserVerService iAuditUserVerService;

    @Override
    public void delAuditUserByNodeId(Long nodeId) throws Exception {
        QueryWrapper<AuditUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("node_id",nodeId);
        List<AuditUser> list=baseMapper.selectList(queryWrapper);
        if(list!=null && list.size()>0) {
            for(AuditUser auditUser:list) {
                AuditUserVer auditUserVer=new AuditUserVer();
                BeanUtil.copyProperties(auditUser, auditUserVer);
                auditUserVer.setVerId(auditUser.getId());
                iAuditUserVerService.save(auditUserVer);
            }
            baseMapper.removeByNodeId(nodeId);
        }
    }

    @Override
    public void batchSaveAuditUser(Long nodeId, String targetObjIds, Integer targetObjType, Long version) {
        List<Long> userIdList=new ArrayList<Long>();
        for(String userIdStr:targetObjIds.split(",")){
            userIdList.add(Long.valueOf(userIdStr));
        }
        batchSaveAuditUser(nodeId, userIdList, targetObjType, version);
    }

    @Override
    public void batchSaveAuditUser(Long nodeId, List<Long> targetObjIdList, Integer targetObjType, Long version) {
        if(targetObjIdList!=null && targetObjIdList.size()>0) {
            for(Long userId : targetObjIdList) {
                AuditUser auditUser=new AuditUser();
                auditUser.setNodeId(nodeId);
                auditUser.setVersion(version);
                auditUser.setTargetObjType(targetObjType);
                auditUser.setTargetObjId(userId);
                save(auditUser);
            }
        }
    }

    @Override
    public AuditUser getAuditUser(Long nodeId, Long targetObjId, Integer targetObjType) {
        QueryWrapper<AuditUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("node_id",nodeId)
                .eq("target_obj_id",targetObjId)
                .eq("target_obj_type",targetObjType);
        List<AuditUser> list=baseMapper.selectList(queryWrapper);
        if(list!=null && list.size()==1)
            return list.get(0);
        return null;

    }

    @Override
    public List<Long> selectIdByNodeIdAndTarGetObjType(Long nodeId, Integer targetObjType) {
        List<AuditUser> auditUsers = selectByNodeIdAndTarGetObjType(nodeId, targetObjType);
        List<Long> longs = new ArrayList<>();
        for (AuditUser auditUser : auditUsers) {
            longs.add(auditUser.getTargetObjId());
        }
        return  longs;
    }

    @Override
    public Map<Long, List<Long>> selectIdByNodeIdAndTarGetObjType(List<Long> nodeIds, Integer targetObjType) {
        LambdaQueryWrapper<AuditUser> wrapper = Wrappers.lambdaQuery();
        wrapper.in(AuditUser::getNodeId,nodeIds).eq(AuditUser::getTargetObjType,targetObjType);
        List<AuditUser> auditUsers = baseMapper.selectList(wrapper);
        Map<Long,List<Long>> listMap = new HashMap<>();
        for (AuditUser auditUser : auditUsers) {
            if (listMap.containsKey(auditUser.getNodeId())){
                listMap.get(auditUser.getNodeId()).add(auditUser.getTargetObjId());
            }else {
                listMap.put(auditUser.getNodeId(),new ArrayList<Long>(){{add(auditUser.getTargetObjId());}});
            }
        }
        return listMap;
    }

    @Override
    public List<Long> selectTargetObjIdByNodeIdAndTarGetObjType(Long nodeId, Integer targetObjType) {
        List<AuditUser> auditUsers = selectByNodeIdAndTarGetObjType(nodeId, targetObjType);
        List<Long> longs = new ArrayList<>();
        for (AuditUser auditUser : auditUsers) {
            longs.add(auditUser.getTargetObjId());
        }
        return longs;
    }

    @Override
    public List<AuditUser> selectByNodeIdAndTarGetObjType(Long nodeId, Integer targetObjType) {
        LambdaQueryWrapper<AuditUser> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AuditUser::getNodeId,nodeId).eq(AuditUser::getTargetObjType,targetObjType);
       return baseMapper.selectList(wrapper);
    }

    @Override
    public List<AuditUser> getAuditUserList(Long nodeId, Integer targetObjType, Long targetObjId) {
        LambdaQueryWrapper<AuditUser> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AuditUser::getNodeId,nodeId).eq(AuditUser::getTargetObjType,targetObjType)
                .eq(AuditUser::getTargetObjId,targetObjId);
        return baseMapper.selectList(wrapper);
    }
}
