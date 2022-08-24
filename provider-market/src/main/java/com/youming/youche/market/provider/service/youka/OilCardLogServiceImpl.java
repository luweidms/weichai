package com.youming.youche.market.provider.service.youka;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.market.api.youka.IOilCardLogService;
import com.youming.youche.market.domain.youka.OilCardLog;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.dto.youca.OilCardLogDto;
import com.youming.youche.market.dto.youca.OilCardManagementDto;
import com.youming.youche.market.provider.mapper.youka.OilCardLogMapper;
import com.youming.youche.market.vo.youca.OilLogVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
* <p>
    *  服务实现类
    * </p>
* @author XXX
* @since 2022-03-24
*/
@DubboService(version = "1.0.0")
    public class OilCardLogServiceImpl extends BaseServiceImpl<OilCardLogMapper, OilCardLog> implements IOilCardLogService {
    @Resource
    private LoginUtils loginUtils;
    @Override
    public void saveOrdUpdate(OilCardLog oilCardLog, int type) {
        oilCardLog.setLogType(type);
        oilCardLog.setLogDate(LocalDateTime.now());
        oilCardLog.setCreateTime(LocalDateTime.now());
        baseMapper.insert(oilCardLog);
    }

    @Override
    public IPage<OilCardLogDto> doQuery(Integer pageNum, Integer pageSize, OilLogVo oilLogVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<OilCardLogDto> pageInfo = new Page<>(pageNum, pageSize);
        LocalDateTime startTime1 = null;
        LocalDateTime endTime1 = null;
        if (oilLogVo.getLogDateBegin() != null && StringUtils.isNotEmpty(oilLogVo.getLogDateBegin())) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = oilLogVo.getLogDateBegin() + " 00:00:00";
            startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
        }
        if (oilLogVo.getLogDateEnd() != null && StringUtils.isNotEmpty(oilLogVo.getLogDateEnd())) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = oilLogVo.getLogDateEnd() + " 23:59:59";
            endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
        }
        return baseMapper.doQuery(pageInfo,oilLogVo,loginInfo.getTenantId(),startTime1,endTime1);
    }
}
