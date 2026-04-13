package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.util.List;

/**
 * 景点分页列表返回数据。
 */
@Data
public class ScenicListRspVO {

    /** 景点列表。 */
    private List<ScenicItemRspVO> list;

    /** 总数量。 */
    private Integer total;

    /** 当前页。 */
    private Integer page;

    /** 每页数量。 */
    private Integer size;
}
