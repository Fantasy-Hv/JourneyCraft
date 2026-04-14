package org.dsgroup.journeycraft.scenic.vo.reqvo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 景点搜索参数。
 */
@Data
public class ScenicSearchReqVO {

    @NotBlank(message = "keyword不能为空")
    private String keyword;

    private Integer type;

    private String city;

    private Integer page = 1;

    private Integer size = 10;
}
