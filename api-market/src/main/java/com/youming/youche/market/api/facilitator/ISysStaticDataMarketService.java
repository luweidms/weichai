package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.base.IBaseService;

import java.util.List;

/**
 * <p>
 * 静态数据表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
public interface ISysStaticDataMarketService extends IBaseService<SysStaticData> {
    /**
     * 获取静态数据
     *
     * @param
     * @return
     * @throws Exception
     */
    List<SysStaticData> getStaticData(String codeType, String accessToken) throws Exception;

    /**
     * 获取静态数据
     *
     * @param codeType  静态枚举类型
     * @param codeValue 静态编码值
     */
    SysStaticData getSysStaticDataCodeName(String codeType, Integer codeValue) throws Exception;

    /**
     * 获取静态数据
     *
     * @param codeType  静态枚举类型
     * @param codeValue 静态编码值
     */
    SysStaticData getSysStaticDataCodeName(String codeType, String codeValue) throws Exception;

    /**
     * 获取静态数据
     *
     * @param codeType 静态枚举类型
     */
    List<SysStaticData> getSysStaticDataList(String codeType, String accessToken);

    /**
     * 获取静态数据
     *
     * @param codeType  静态枚举类型
     * @param codeValue 静态编码值
     */
    SysStaticData getSysStaticData(String codeType, String codeValue, String accessToken) throws Exception;

    /**
     * 获取静态数据 -- 静态编码名称
     *
     * @param codeType  静态枚举类型
     * @param codeValue 静态编码值
     */
    String getSysStaticDatas(String codeType, Integer codeValue);

    /**
     * 获取静态数据
     *
     * @param facilitatorLength 静态枚举类型
     */
    List<SysStaticData> getSysStaticDataListByCodeName(String facilitatorLength);

    /**
     * 获取静态数据
     *
     * @param codeType 静态枚举类型
     * @param codeName 静态编码名称
     */
    SysStaticData getSysSyaticDataByCodeNameAndCodeType(String codeType, String codeName);

}
