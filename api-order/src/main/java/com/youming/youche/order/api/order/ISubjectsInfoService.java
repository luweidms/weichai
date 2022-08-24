package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.SubjectsInfo;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface ISubjectsInfoService extends IBaseService<SubjectsInfo> {
    /**
     * 查询科目数据
     * @param subjectsIds
     * @param tenantId
     * @return
     * @throws Exception
     */
     Map<Long, SubjectsInfo> getSubjectsInfo(Long[] subjectsIds, Long tenantId);


    /**
     * 查询科目数据
     * @param subjectsId
     * @param tenantId
     * @return
     * @throws Exception
     */
     SubjectsInfo getSubjectsInfo(Long subjectsId,Long tenantId);


    /**
     * 获取指定科目名称
     *
     * @param sujectId busiType
     * @param
     * @return
     */
     String getSubjectName(Long sujectId);
}
