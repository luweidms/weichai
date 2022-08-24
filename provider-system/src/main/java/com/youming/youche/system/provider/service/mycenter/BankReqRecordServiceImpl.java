package com.youming.youche.system.provider.service.mycenter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.mycenter.IBankReqRecordService;
import com.youming.youche.system.domain.mycenter.CmbReqRecord;
import com.youming.youche.system.provider.mapper.mycenter.BankReqRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName BankReqRecordServiceImpl
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/2 16:07
 */
@DubboService(version = "1.0.0")
public class BankReqRecordServiceImpl extends BaseServiceImpl<BankReqRecordMapper, CmbReqRecord> implements IBankReqRecordService {

    private static final Logger log = LoggerFactory.getLogger(BankReqRecordServiceImpl.class);

    @Override
    public void addRecord(String reqNo, String tranFunc, String reqData, String respNo, String respCode, String respMsg, String respData) {
        try {
            CmbReqRecord cmbReqRecord = new CmbReqRecord();
            cmbReqRecord.setReqNo(reqNo);
            cmbReqRecord.setTranFunc(tranFunc);
            cmbReqRecord.setReqData(reqData);
            cmbReqRecord.setRespNo(respNo);
            cmbReqRecord.setRespCode(respCode);
            cmbReqRecord.setRespMsg(respMsg);
            cmbReqRecord.setRespData(respData);
            int rows = baseMapper.insert(cmbReqRecord);
            if (rows > 0) {
                log.info("[CMB][银行接口请求日志记录成功]");
            }else {
                log.info("[CMB][银行接口请求日志记录失败]");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[CMB][银行接口请求日志记录异常]"+e.getMessage());
        }
    }
}
