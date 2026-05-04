package org.dsgroup.journeycraft.favorite.vo.reqvo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新收藏夹请求 VO。
 */
@Data
public class CollectionUpdateReqVO {

    /**
     * 新的收藏夹名称 (最长 20 字符)
     */
    @NotBlank(message = "收藏夹名称必填")
    @Size(max = 20, message = "名称最长 20 字符")
    private String collectionName;
}
