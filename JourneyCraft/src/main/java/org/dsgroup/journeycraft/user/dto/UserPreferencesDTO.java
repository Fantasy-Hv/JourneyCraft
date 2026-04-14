package org.dsgroup.journeycraft.user.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户偏好数据结构。
 */
@Data
public class UserPreferencesDTO {

    private List<String> interests;

    private String transportType;

    private List<String> foodPreferences;

    private Integer maxWalkDistance;

    private Integer budgetPerDay;
}
