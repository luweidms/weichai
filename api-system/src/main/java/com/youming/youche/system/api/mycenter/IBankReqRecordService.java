package com.youming.youche.system.api.mycenter;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.mycenter.CmbReqRecord;

/**
 * @InterfaceName IBankReqRecordService
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/2 16:02
 */
public interface IBankReqRecordService extends IBaseService<CmbReqRecord> {

    /** 添加交易日志
     * */
    void addRecord(String reqNo,String tranFunc,String reqData,String respNo,String respCode,String respMsg,String respData);
}