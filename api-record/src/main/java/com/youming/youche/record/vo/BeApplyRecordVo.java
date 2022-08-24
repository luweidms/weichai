package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @version:
 * @Title: BeApplyRecordVo
 * @Package: com.youming.youche.record.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/25 13:41
 * @company:
 */
@Data
public class BeApplyRecordVo implements Serializable {

    private Long id;

    private String mobilePhone;

    private String userPrice;

    private Integer applyCarUserType;

    private String applyCarUserTypeName;

    private Long beApplyTenantId;

    private String tenantName;

    private String tennantLinkMan;

    private String tennantLinkPhone;

    private String createDate;

    private Integer state;
    private String stateName;

    private String applyRemark;

    private String applyFile;

    private String auditRemark;

    private String auditDate;

    private String newApplyId;

    private Long userId;

    private Long tenantId;

    private Long applyDriverUserId;

    private String applyDriverPhone;

    private String applyDriverName;

    private String applyPlateNumbers;

    private Integer vehicleNum;

    private List<BeApplyRecordVehicleVo> vehicleList;

    private List checkedVehicles;

    private String applyFileUrl;

    private String applyFileBigUrl;

}
