package org.dsgroup.journeycraft.favorite.vo.reqvo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量删除收藏请求 VO。
 */
@Data
public class FavoriteBatchDeleteReqVO {

    /**
     * 收藏记录 ID 列表 (最多 100 个)
     */
    @NotEmpty(message = "收藏 ID 列表不能为空")
    @Size(max = 100, message = "最多删除 100 条")
    private List<Long> ids;
}
