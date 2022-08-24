package com.youming.youche.record.vo.driver;

import com.youming.youche.record.domain.trailer.TenantTrailerRel;
import com.youming.youche.record.domain.trailer.TenantTrailerRelVer;
import com.youming.youche.record.domain.trailer.TrailerLineRel;
import com.youming.youche.record.domain.trailer.TrailerManagement;
import com.youming.youche.record.domain.trailer.TrailerManagementVer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Date:2022/1/20
 */
@Data
public class TrailerManagementVo implements Serializable {

    private TrailerManagement trailer;

    private TrailerManagementVer trailerManagementVer;

    private TenantTrailerRel trailerRel;

    private TenantTrailerRelVer trailerHis;

    private String lineRelStr;

    private List<TrailerLineRel> trailerLineRelList;

}
