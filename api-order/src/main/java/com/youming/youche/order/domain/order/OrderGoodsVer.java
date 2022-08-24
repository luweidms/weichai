package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderGoodsVer extends BaseDomain {

    private static final long serialVersionUID = 1L;



    /**
     * 详细地址
     */
    private String addrDtl;
    /**
     * 常用办公地址
     */
    private String address;
    /**
     * 城市id
     */
    private Integer cityId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 线路合同ID
     */
    private Long contractId;
    /**
     * 合同图片url
     */
    private String contractUrl;
    /**
     * 县区id
     */
    private Integer countyId;
    /**
     * 客户类型：1 固定线路的客户，0 临时线路的客户
     */
    private Integer custType;
    /**
     * 客户名称
     */
    private String customName;
    /**
     * 客户单号，多个用换行符分隔
     */
    private String customNumber;
    /**
     * 客户单号（跟cutom_id的关系）
     */
    private String customOrderId;
    /**
     * 客户id
     */
    private Long customUserId;
    /**
     * 目的地详情
     */
    private String des;
    /**
     * 目的地详细地址
     */
    private String desDtl;
    /**
     * 起始经度
     */
    private String eand;
    /**
     * 目的经度
     */
    private String eandDes;
    /**
     * 来源租户id
     */
    private Long fromTenantId;
    /**
     * 货物名称
     */
    private String goodsName;
    /**
     * 货品类型
     */
    private Integer goodsType;

    /**
     * 联系人名称
     */
    private String lineName;
    /**
     * 联系人电话
     */
    private String linePhone;
    /**
     * 合同状态
     */
    private Integer loadState;
    /**
     * 驻场调度手机
     */
    private String localPhone;
    /**
     * 驻场调度人员 id
     */
    private Long localUser;
    /**
     * 驻场人名
     */
    private String localUserName;
    /**
     * 起始纬度
     */
    private String nand;
    /**
     * 目的纬度
     */
    private String nandDes;
    /**
     * 文字描述目的地址，不填写默认使用百度定位地址,做为百度地址的备注
     */
    private String navigatDesLocation;
    /**
     * 文字描述出发地址，不填写默认使用百度定位地址,做为百度地址的备注
     */
    private String navigatSourceLocation;
    /**
     * 货物件数
     */
    private Integer nums;
    /**
     * 操作员id
     */
    private Long opId;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 省
     */
    private Integer provinceId;
    /**
     * 回单人id
     */
    private Long receiptsUserId;
    /**
     * 回单人姓名
     */
    private String receiptsUserName;
    /**
     * 回单地址
     */
    private String reciveAddr;
    /**
     * 回单地址-市
     */
    private Long reciveCityId;
    /**
     * 收货人
     */
    private String reciveName;
    /**
     * 收货电话
     */
    private String recivePhone;
    /**
     * 回单地址-省
     */
    private Long reciveProvinceId;
    /**
     * 回单状态
     */
    private Integer reciveState;
    /**
     * 回单类型
     */
    private Integer reciveType;

    /**
     * 始发地详情
     */
    private String source;
    /**
     * 货物体积
     */
    private Float square;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 修改数据的操作人id
     */
    private Long updateOpId;
    /**
     * 需求车长
     */
    private String vehicleLengh;
    /**
     * 需求车辆类型
     */
    private Integer vehicleStatus;
    /**
     * 货物重量kg
     */
    private Float weight;
    /**
     * 财务编码
     */
    private String yongyouCode;
    /**
     * 联系人
     */
    @TableField(exist = false)
    private String linkName;
    /**
     * 需求车长名称
     */
    @TableField(exist = false)
    private String vehicleLenghName;
    /**
     * 回单类型
     */
    @TableField(exist = false)
    private String reciveTypeName;

    public String getReciveTypeName() {
        if(this.reciveType == null){
            this.reciveTypeName = "";
        }else if(this.reciveType == 1){
            this.reciveTypeName = "电子回单";
        }else if(this.reciveType == 2){
            this.reciveTypeName = "纸质+电子回单";
        }
        return reciveTypeName;
    }

    public void setReciveTypeName(String reciveTypeName) {
        this.reciveTypeName = reciveTypeName;
    }

//    public String getVehicleLenghName() {
//        this.vehicleLenghName = this.vehicleLengh+"米";
//        return vehicleLenghName;
//    }
//
//    public void setVehicleLenghName(String vehicleLenghName) {
//        this.vehicleLenghName = vehicleLenghName;
//    }

    @TableField(exist = false)
    private String linkPhone;
    @TableField(exist = false)
    private String goodsTypeName;
    @TableField(exist = false)
    private String vehicleStatusName;
    /**
     * 回单地址的省份名称
     */
    @TableField(exist = false)
    private String reciveProvinceName;
    /**
     * 回单地址的市名称
     */
    @TableField(exist = false)
    private String reciveCityName;

    public String getVehicleStatusName() {
        if(this.vehicleStatus == null){
            this.vehicleStatusName = "";
        }else if(this.vehicleStatus == 1){
            this.vehicleStatusName = "厢车";
        }else if(this.vehicleStatus == 2){
            this.vehicleStatusName = "平板";
        }else if(this.vehicleStatus == 3){
            this.vehicleStatusName = "高栏";
        }else if(this.vehicleStatus == 4){
            this.vehicleStatusName = "冷柜";
        }else if(this.vehicleStatus == 5){
            this.vehicleStatusName = "其他车";
        }else if(this.vehicleStatus == 7){
            this.vehicleStatusName = "油罐车";
        }else if(this.vehicleStatus == 8){
            this.vehicleStatusName = "轿运车";
        }else if(this.vehicleStatus == 9){
            this.vehicleStatusName = "奶罐车";
        }else if(this.vehicleStatus == 10){
            this.vehicleStatusName = "小货车";
        }else if(this.vehicleStatus == 11){
            this.vehicleStatusName = "摆渡车";
        }else {
            this.vehicleStatusName = "";
        }
        return vehicleStatusName;
    }

    public void setVehicleStatusName(String vehicleStatusName) {
        this.vehicleStatusName = vehicleStatusName;
    }

    public String getGoodsTypeName() {
        if(this.goodsType == null){
            this.goodsTypeName = "";
        }else if(this.goodsType == 1){
            this.goodsTypeName = "普货";
        }else if(this.goodsType == 2){
            this.goodsTypeName = "危险品";
        }
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }
}
