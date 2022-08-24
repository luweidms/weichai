package com.youming.youche.record.provider.mapper.oa;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.oa.OaFiles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022/1/10 16:37
 */
public interface OaFilesMapper extends BaseMapper<OaFiles> {

    List<OaFiles> getRepairOrderPicList(@Param("flowId") long flowId);

    void deletePicFileIds(@Param("flowId") long flowId,
                          @Param("reltype6") int reltype6,
                          @Param("fileName") String fileName);

    /**
     * 查询 借支报销文件表
     *
     * @param relId   关联ID（关联表主键ID）
     * @param relType 关联数据类型:1借支 2借支核销 3报销 6：维修保养
     * @return
     */
    List<OaFiles> getRepairOrderPicList2(@Param("relId") long relId, @Param("relType") String relType);

    void insertRecord(@Param("oaFiles") OaFiles oaFiles);

}
