package com.youming.youche.market.provider.service.repair;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.api.repair.IRepairItemsService;
import com.youming.youche.market.domain.repair.RepairItems;
import com.youming.youche.market.dto.user.RepairItemsDto;
import com.youming.youche.market.provider.mapper.repair.RepairItemsMapper;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.domain.SysAttach;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维修项
 *
 * @author hzx
 * @date 2022/3/14 10:16
 */
@DubboService(version = "1.0.0")
@Service
public class RepairItemsServiceimpl extends BaseServiceImpl<RepairItemsMapper, RepairItems> implements IRepairItemsService {

    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;
    @Resource
    private ReadisUtil readisUtil;

    @Resource
    RedisUtil redisUtil;

    @Override
    @Transactional
    public List<RepairItemsDto> getRepairItems(Long repairId) {

        LambdaQueryWrapper<RepairItems> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RepairItems::getRepairId, repairId);
        List<RepairItems> list = getBaseMapper().selectList(queryWrapper);

        List<RepairItemsDto> itemList = new ArrayList<RepairItemsDto>();
        for (RepairItems repairItems : list) {
            RepairItemsDto rio = new RepairItemsDto();

            BeanUtil.copyProperties(repairItems, rio);
            rio.setRepairRootIdStr(getSysStaticData("REPAIR_ROOT_ID", rio.getRepairRootId()).getCodeName());

            List<String> repairPicUrl = new ArrayList<String>();
            String repairPicIds = repairItems.getRepairPicIds();
            if (StringUtils.isNotBlank(repairPicIds)) {
                String[] split = repairPicIds.split(",");
                for (String s : split) {
                    String imgUrl = getImgUrl(Long.valueOf(s));
                    repairPicUrl.add(imgUrl);
                }
            }
            rio.setRepairPicUrls(repairPicUrl);
            itemList.add(rio);
        }
        return itemList;
    }

    @Override
    public List<RepairItems> getRepairItemsList(Long tenantId, Long repairId) {
        LambdaQueryWrapper<RepairItems> lambda = Wrappers.lambdaQuery();
        if (tenantId != null && tenantId > 0) {
            lambda.eq(RepairItems::getTenantId, tenantId);
        }
        lambda.eq(RepairItems::getRepairId, repairId);
        return this.list(lambda);
    }

    @Override
    public void delRepairItems(RepairItems repairItems) {
        this.remove(repairItems.getId());
    }

    @Override
    public List<Map<String, Object>> getLevelOneRepair() {
        List<SysStaticData> sysStaticDatas = readisUtil.getSysStaticDataList(EnumConsts.SysStaticData.REPAIR_ROOT_ID);
        List<Map<String, Object>> list = new ArrayList<>();
        if (sysStaticDatas != null && sysStaticDatas.size() > 0) {
            for (SysStaticData sysStaticData : sysStaticDatas) {
                Map<String, Object> map = new HashMap<>();
                map.put("codeValue", sysStaticData.getCodeValue());
                map.put("codeName", sysStaticData.getCodeName());
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getLevelTwoRepair() {
        List<SysStaticData> sysStaticDatas = readisUtil.getSysStaticDataList(EnumConsts.SysStaticData.REPAIR_CHILD_ID);
        List<Map<String, Object>> list = new ArrayList<>();
        if (sysStaticDatas != null && sysStaticDatas.size() > 0) {
            for (SysStaticData sysStaticData : sysStaticDatas) {
                Map<String, Object> map = new HashMap<>();
                map.put("codeValue", sysStaticData.getCodeValue());
                map.put("codeName", sysStaticData.getCodeName());
                map.put("codeId", sysStaticData.getCodeId());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 获取图片URL
     *
     * @param flowId
     */
    public String getImgUrl(Long flowId) {
        String url = "";
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();
            if (flowId != null && flowId > 0) {
                SysAttach sysAttach = iSysAttachService.getById(flowId);
                if (sysAttach != null) {
                    String tag = sysAttach.getStorePath().substring(sysAttach.getStorePath().lastIndexOf(".") + 1);
                    String urls = client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0];
                    url = urls.substring(0, urls.lastIndexOf(".")) + "_big." + tag;
                    return url;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

}
