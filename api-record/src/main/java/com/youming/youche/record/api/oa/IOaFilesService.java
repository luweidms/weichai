package com.youming.youche.record.api.oa;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.oa.OaFiles;

import java.util.List;

/**
 * @author hzx
 * @date 2022/4/18 14:14
 */
public interface IOaFilesService extends IBaseService<OaFiles> {

    /**
     * 查询维修保养关联文件信息
     */
    List<OaFiles> getRepairOrderPicList(List<Long> flowIdList);

    /**
     * 查询关联文件信息
     */
    List<OaFiles> queryOaFilesById(Long LId);

    /**
     * 查询维修保养关联文件信息
     */
    List<OaFiles> getRepairOrderPicListById(Long flowId);

}
