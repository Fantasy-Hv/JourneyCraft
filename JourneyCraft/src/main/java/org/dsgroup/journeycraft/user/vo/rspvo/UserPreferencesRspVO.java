package org.dsgroup.journeycraft.user.vo.rspvo;

import lombok.Data;

import java.util.List;

/**
 * 用户偏好响应数据。
 */
@Data
public class UserPreferencesRspVO {

    private List<String> interests;

    private String transportType;

    private List<String> foodPreferences;

    private Integer maxWalkDistance;

    private Integer budgetPerDay;
}
