package com.youming.youche.record.provider.service.impl.account;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.record.api.account.IUnionBankInfoService;
import com.youming.youche.record.domain.account.UnionBankInfo;
import com.youming.youche.record.provider.mapper.account.UnionBankInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 大小额行号表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
@DubboService(version = "1.0.0")
public class UnionBankInfoServiceImpl extends BaseServiceImpl<UnionBankInfoMapper, UnionBankInfo> implements IUnionBankInfoService {

    @Resource
    RedisUtil redisUtil;

    @Override
    public List<UnionBankInfo> getUnionBankInfoList(String cityCode, String bankId, String keyWord) {
        cityCode = cityCode.substring(0, 4);
        SysStaticData bankType = getSysStaticData("BANK_TYPE", bankId);
        String bankClsCode = bankType.getCodeId() + "";
        String bankName = bankType.getCodeName();

        List<UnionBankInfo> unionBankInfoList = this.getUnionBankInfoList(cityCode, bankClsCode, bankName, keyWord);

        return unionBankInfoList;
    }

    @Override
    public List<UnionBankInfo> getUnionBankInfoList(String cityCode, String bankClsCode, String bankName, String keyWord) {
        //通过地市编码 以及总行银行行别编码 以及总行名称模糊匹配
        if (StringUtils.isEmpty(cityCode)) {
            throw new BusinessException("地市信息不能为空");
        }
        if (StringUtils.isEmpty(bankClsCode)) {
            throw new BusinessException("银行信息不能为空");
        }
        if (StringUtils.isEmpty(bankName)) {
            throw new BusinessException("银行信息不能为空");
        }


        LambdaQueryWrapper<UnionBankInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnionBankInfo::getCityCode, cityCode);
        queryWrapper.eq(UnionBankInfo::getBankClsCode, bankClsCode);
        queryWrapper.and(wq -> wq.like(UnionBankInfo::getBankDisplayName, "%" + bankName + "%" + keyWord + "%")
                .or().eq(UnionBankInfo::getBankDisplayName, keyWord));

        List<UnionBankInfo> list = this.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            LambdaQueryWrapper<UnionBankInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UnionBankInfo::getCityCode, cityCode);
            wrapper.eq(UnionBankInfo::getBankClsCode, bankClsCode);
            String partName = bankName.replaceAll("股份有限公司", "");//对于名称不一致的情况，去掉股份有限公司继续查询
            wrapper.and(wq -> wq.like(UnionBankInfo::getBankDisplayName, "%" + partName + "%" + keyWord + "%")
                    .or().eq(UnionBankInfo::getBankDisplayName, keyWord));
            list = this.list();
        }
        return list;
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
