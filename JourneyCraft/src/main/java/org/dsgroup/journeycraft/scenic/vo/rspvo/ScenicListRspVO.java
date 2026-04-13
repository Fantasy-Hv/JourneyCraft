package org.dsgroup.journeycraft.scenic.vo.rspvo;

import lombok.Data;

import java.util.List;

/**
 * 景点分页列表响应数据。
 */
@Data
public class ScenicListRspVO {

    private List<ScenicItemRspVO> list;

    private Integer total;

    private Integer page;

    private Integer size;
}
