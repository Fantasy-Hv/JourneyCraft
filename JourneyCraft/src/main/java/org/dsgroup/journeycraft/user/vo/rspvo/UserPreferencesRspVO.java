package org.dsgroup.journeycraft.user.vo.rspvo;

import lombok.Data;

import java.util.List;

/**
 * 用户偏好返回数据。
 */
@Data
public class UserPreferencesRspVO {

    /** 兴趣标签。 */
    private List<String> interests;

    /** 出行方式。 */
    private String transportType;

    /** 饮食偏好。 */
    private List<String> foodPreferences;

    /** 最大步行距离，单位米。 */
    private Integer maxWalkDistance;

    /** 日均预算。 */
    private Integer budgetPerDay;
}
