package com.youming.youche.finance.provider.service.ac;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.finance.api.ac.ICmSalaryTemplateService;
import com.youming.youche.finance.commons.util.DateUtil;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;
import com.youming.youche.finance.domain.ac.CmTemplateField;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryTemplateMapper;
import com.youming.youche.finance.provider.mapper.ac.CmTemplateFieldMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/11 16:58
 */
@DubboService(version = "1.0.0")
public class CmSalaryTemplateService extends BaseServiceImpl<CmSalaryTemplateMapper, CmSalaryTemplate>
        implements ICmSalaryTemplateService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private CmTemplateFieldMapper cmTemplateFieldMapper;

    @Override
    public List<CmTemplateField> getMaxTemplateFiel(String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" +  accessToken);
        Long tenantId = loginInfo.getTenantId();

        QueryWrapper<CmSalaryTemplate> cmSalaryTemplateQueryWrapper = new QueryWrapper<>();
        cmSalaryTemplateQueryWrapper
                .eq("tenant_id", tenantId)
                .or()
                .eq("tenant_id", -1)
                .or()
                .isNull("tenant_id");
        cmSalaryTemplateQueryWrapper.orderByDesc("template_month", "id");
        cmSalaryTemplateQueryWrapper.last("limit 1");

        CmSalaryTemplate cmSalaryTemplate = baseMapper.selectOne(cmSalaryTemplateQueryWrapper);

        QueryWrapper<CmTemplateField> cmTemplateFieldQueryWrapper = new QueryWrapper<>();
        cmTemplateFieldQueryWrapper.eq("template_id", cmSalaryTemplate.getId());
        cmTemplateFieldQueryWrapper.orderByAsc("field_index");


        return cmTemplateFieldMapper.selectList(cmTemplateFieldQueryWrapper);
    }

    @Override
    public List<CmTemplateField> getTemplateAllField(String accessToken, String settleMonth) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" +  accessToken);
        Long tenantId = loginInfo.getTenantId();

        List<CmTemplateField> cmTemplateFields = new ArrayList<CmTemplateField>();

        boolean getTempTmp = false;//是否获取兼容模板
        if(getTempTmp && Long.valueOf(settleMonth.replace("-", "")) < 201811) {
            getTempTmp = true;
        } else if ("2018-11".equals(settleMonth)) {
            if((tenantId + "").equals("8") || (tenantId+"").equals("11") || (tenantId+"").equals("18") || (tenantId+"").equals("89") || (tenantId+"").equals("137")) {
                getTempTmp = true;
            }
        }

        if (getTempTmp) {
            cmTemplateFields = getCmSalaryTemplateByTmpId();
        } else {
            //必走下面的逻辑
            //此处和产品沟通  每次查最新的  所以这个settleMonth采用最新的
            Date date = new Date();
            settleMonth = DateUtil.formatDate(date, DateUtil.YEAR_MONTH_FORMAT);

            cmTemplateFields = getCmSalaryTemplateByMon(tenantId, settleMonth);
        }
        return cmTemplateFields;
    }

    private List<CmTemplateField> getCmSalaryTemplateByTmpId() {
        QueryWrapper<CmSalaryTemplate> cmSalaryTemplateQueryWrapper = new QueryWrapper<>();
        cmSalaryTemplateQueryWrapper.eq("id", 2);
        cmSalaryTemplateQueryWrapper.last("limit 1");

        CmSalaryTemplate cmSalaryTemplate = baseMapper.selectOne(cmSalaryTemplateQueryWrapper);

        QueryWrapper<CmTemplateField> cmTemplateFieldQueryWrapper = new QueryWrapper<>();
        cmTemplateFieldQueryWrapper.eq("template_id", cmSalaryTemplate.getId());
        cmTemplateFieldQueryWrapper.eq("is_select", 2);
        cmTemplateFieldQueryWrapper.orderByAsc("field_index");

        return cmTemplateFieldMapper.selectList(cmTemplateFieldQueryWrapper);
    }

    private List<CmTemplateField> getCmSalaryTemplateByMon(long tenantId,String month) {
        QueryWrapper<CmSalaryTemplate> cmSalaryTemplateQueryWrapper = new QueryWrapper<>();
        cmSalaryTemplateQueryWrapper.le("template_month", month);
        cmSalaryTemplateQueryWrapper.and(i -> i.eq("tenant_id", tenantId).or().eq("tenant_id", -1).or().isNull("tenant_id"));
        cmSalaryTemplateQueryWrapper.orderByDesc("template_month", "id");
        cmSalaryTemplateQueryWrapper.last("limit 1");

        CmSalaryTemplate cmSalaryTemplate = baseMapper.selectOne(cmSalaryTemplateQueryWrapper);

        QueryWrapper<CmTemplateField> cmTemplateFieldQueryWrapper = new QueryWrapper<>();
        cmTemplateFieldQueryWrapper.eq("template_id", cmSalaryTemplate.getId());
        cmTemplateFieldQueryWrapper.eq("is_select", 1);
        cmTemplateFieldQueryWrapper.orderByAsc("field_index");

        return cmTemplateFieldMapper.selectList(cmTemplateFieldQueryWrapper);
    }

    @Override
    public List<CmTemplateField> getCmSalaryTemplateByMon(Long tenantId, String month) {
        List<CmTemplateField> cmTemplateFields = new ArrayList<CmTemplateField>();
        boolean getTempTmp = false;//是否获取兼容模板
        if(getTempTmp&&Long.valueOf(month.replace("-", ""))<201811){
            getTempTmp=true;
        }else if("2018-11".equals(month)){
            if((tenantId+"").equals("8")||(tenantId+"").equals("11")||(tenantId+"").equals("18")||(tenantId+"").equals("89")||(tenantId+"").equals("137")){
                getTempTmp=true;
            }
        }

        if(getTempTmp){
            cmTemplateFields =cmTemplateFieldMapper.getCmSalaryTemplateByTmpId();//兼容模板
        }else{
            cmTemplateFields = cmTemplateFieldMapper.getCmSalaryTemplateByMon(tenantId,month);
        }
        return cmTemplateFields;
    }
}
