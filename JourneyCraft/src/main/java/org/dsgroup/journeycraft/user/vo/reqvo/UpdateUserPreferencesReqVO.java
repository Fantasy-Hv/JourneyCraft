package org.dsgroup.journeycraft.user.vo.reqvo;

import lombok.Data;

import java.util.List;

/**
 * 更新用户偏好请求参数。
 */
@Data
public class UpdateUserPreferencesReqVO {

    private List<String> interests;

    private String transportType;

    private List<String> foodPreferences;

    private Integer maxWalkDistance;

    private Integer budgetPerDay;
}
