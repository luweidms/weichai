package com.youming.youche.record.vo.driver;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.record.domain.user.UserLineRelVer;
import com.youming.youche.record.domain.user.UserSalaryInfoVer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @version:
 * @Title: DoAddDriverVo
 * @Package: com.youming.youche.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/18 17:21
 * @company:
 */
@Data
public class DoAddDriverVo implements Serializable {

    private Long userId;

    private Long relId;

    /**
     * 	司机种类 (-1 全部；1:公司自有司机；2:业务招商司机；3:临时外调司机；4:外来挂靠司机)
     */
    private String carUserType;

    /**
     * 	线路绑定，内嵌对象
     */
    private List<LineRels> lineRels;

    /**
     * 薪资模式设置列表，里程区间价格，按趟区间价格
     */
    private List<UserSalaryInfoVo> userSalaryInfoList;

    private List<UserSalaryInfoVer> userSalaryInfoVerList;

    private List<UserLineRelVer> userLineRelVerList;

    /**
     *薪资模式(1:普通模式 2:里程模式 3:按趟模式)
     */
    private Integer salaryPattern;

    /**
     *司机账号
     */
    private String loginAcct;

    /**
     *司机名称
     */
    private String linkman;

    /**
     * 身份证
     */
    private String identification;


    /**
     * 	驾驶证号
     */
    private String adriverLicenseSn;

    /**
     * 	准驾车型
     */
    private String vehicleType;


    /**
     *	驾驶证有效期开始日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String driverLicenseTime;
    /**
     *	驾驶证有效期结束日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String driverLicenseExpiredTime;

    /**
     *	从业资格证有效期开始日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String qcCertiTime;

    /**
     *从业资格有效期结束日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String qcCertiExpiredTime;

    /**
     * 司机头像id
     */
    private Long userPrice;

    /**
     * 	司机头像地址
     */
    private String userPriceUrl;

    /**
     *	身份证正面id
     */
    private Long idenPictureFront;

    /**
     *	身份证正面地址
     */
    private String idenPictureFrontUrl;

    /**
     *	身份证反面id
     */
    private Long idenPictureBack;

    /**
     *	身份证反面地址
     */
    private String idenPictureBackUrl;

    /**
     *驾驶证正本id
     */
    private Long adriverLicenseOriginal;

    /**
     *	驾驶证正本地址
     */
    private String adriverLicenseOriginalUrl;

    /**
     *	驾驶证副本id
     */
    private Long adriverLicenseDuplicate;

    /**
     *驾驶证副本地址
     */
    private String adriverLicenseDuplicateUrl;

    /**
     *	从业资格证id
     */
    private Long qcCerti;

    /**
     *	从业资格证地址
     */
    private String qcCertiUrl;


    /**
     *	路哥运输协议id
     */
    private Long lugeAgreement;


    /**
     *	路哥运输协议地址
     */
    private String lugeAgreementUrl;


    /**
     *	归属部门
     */
    private Long attachedOrgId;


    /**
     *	归属人id
     */
    private Long attachedUserId;


    /**
     *	归属人名称
     */
    private String attachedUserName;


    /**
     *	基本工资
     */
    private Long salary;


    /**
     *补贴金额
     */
    private Long subsidy;

    private Boolean ocrFlag;

    private String reasonFailure;

    public String getCarUserTypeName() {
        if (carUserType != null) {
            if (carUserType.equals("1")) {
                return "公司自有司机";
            } else if (carUserType.equals("2")) {
                return "业务招商司机";
            } else if (carUserType.equals("3")) {
                return "临时外调司机";
            } else {
                return "";
            }
        }
        return "";
    }

}
