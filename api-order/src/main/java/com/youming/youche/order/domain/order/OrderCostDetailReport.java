package com.youming.youche.order.domain.order;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.IEtcMaintainService;
import com.youming.youche.order.domain.OilCardManagement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author xxx
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderCostDetailReport extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 上报金额
     */
    private Long amount;

    @TableField(exist = false)
    private String amountStr;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 附件
     */
    private Long fileId;

    /**
     * 附件1
     */
    private Long fileId1;

    /**
     * 附件2
     */
    private Long fileId2;

    /**
     * 附件3
     */
    private Long fileId3;

    /**
     * 附件4
     */
    private Long fileId4;

    /**
     * 附件地址
     */
    private String fileUrl;

    /**
     * 附件地址1
     */
    private String fileUrl1;

    /**
     * 附件地址2
     */
    private String fileUrl2;

    /**
     * 附件地址3
     */
    private String fileUrl3;

    /**
     * 附件地址4
     */
    private String fileUrl4;

    /**
     * 是否可以审核 0否 1是 枚举SysStaticDataEnum.IS_AUTH
     */
    private Integer isAudit;

    /**
     * 油耗里程
     */
    private Long oilMileage;

    @TableField(exist = false)
    private String oilMileageStr;

    /**
     * 临时油站名称
     */
    private String oilProductName;

    /**
     * 加油升数
     */
    private Double oilRise;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 付款方式 1:油卡 2:现金 3:ETC卡
     */
    private Integer paymentWay;

    /**
     * 主表id
     */
    private Long relId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 显示顺序
     */
    private Integer sortId;

    /**
     * 上报状态 0未提交 1已提交 2待审核 3审核中 4审核不通过 5审核通过
     */
    private Integer state;

    /**
     * 提交时间
     */
    private LocalDateTime subTime;

    /**
     * 提交人
     */
    private Long subUserId;

    /**
     * 提交人名称
     */
    private String subUserName;

    /**
     * 数据类型 1油费 2路桥费
     */
    private Integer tableType;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 司机id
     */
    private Long userId;

    /**
     * 费用类型id
     */
    private Long costTypeId;

    /**
     * 费用类型名称
     */
    private String costTypeName;

    @TableField(exist = false)
    private Boolean isDel = true;


    public void setSubInfo(String operation, Date date, LoginInfo loginInfo) {

        this.setTenantId(loginInfo.getTenantId());
        //保存
        if(operation.equals("save")){
            if(null == this.getCreateTime()){

                this.setCreateTime(date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
            }
            this.setState(0);
            this.setIsAudit(0);
        }else if(operation.equals("submit")){
            this.setSubUserId(loginInfo.getId());
            this.setSubUserName(loginInfo.getName());
            if(null == this.getCreateTime()){
                this.setCreateTime(date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
            }
            this.setSubTime(date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
            this.setState(3);
            this.setIsAudit(1);
        }
    }
    @Resource
    IOilCardManagementService iOilCardManagementService;
    @Resource
    IEtcMaintainService iEtcMaintainService;
    public void checkData(int tableType, String typeName, Long tenantId) {
        if(null == amount || amount < 0){
            throw new BusinessException(typeName+"第["+sortId+"]项消费金额不能为空！");
        }
        if(1 == tableType){
            if(null == oilRise || oilRise < 0){
                throw new BusinessException(typeName+"第["+sortId+"]项加油升数不能为空！");
            }
            /*if(null == oilMileage || oilMileage < 0){
                throw new BusinessException(typeName+"第["+sortId+"]项加油里程不能为空！");
            }*/
        }
        if(null == paymentWay || paymentWay < 0){
            throw new BusinessException(typeName+"第["+sortId+"]项支付方式不能为空！");
        }
        if(1 == tableType && paymentWay == 1){
            //校验卡号是否存在
            List<OilCardManagement> oilCardManagementList = iOilCardManagementService.getOilCardManagementByCard(cardNo,tenantId);
            if(null == oilCardManagementList || oilCardManagementList.isEmpty()){
                throw new BusinessException(typeName+"第["+sortId+"]项油卡不存在！");
            }
        }else if(1 == tableType && paymentWay == 4) {
            if(StringUtils.isBlank(oilProductName)||oilProductName.length()>50) {
                throw new BusinessException(typeName+"第["+sortId+"]项油站名称不能为空或长度过长！");
            }
        }else if(2 == tableType && paymentWay == 3){
            //校验卡号是否存在
            List<Map>  mapList = iEtcMaintainService.checkEtcCode(cardNo);
            if(null == mapList || mapList.isEmpty()){
                throw new BusinessException(typeName+"第["+sortId+"]ETC卡不存在！");
            }
        }
        if((paymentWay == 1 || paymentWay == 3) && StringUtils.isBlank(cardNo)){
            throw new BusinessException(typeName+"第["+sortId+"]项卡号不能为空！");
        }

        if((null == fileId || fileId < 0 ) && (null == fileId1 || fileId1 < 0) && (null == fileId2 || fileId2 < 0)){
            throw new BusinessException(typeName+"第["+sortId+"]请至少上传一个附件！");
        }
    }
}
