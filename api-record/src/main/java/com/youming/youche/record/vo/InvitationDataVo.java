package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2021/12/23
 */
@Data
public class InvitationDataVo implements Serializable {

     /**
      *被邀请的数量
      */
    private Long inviteToMeVehicles;

    /**
     *我邀请的数量
     */
    private Long inviteVehicles;
}
