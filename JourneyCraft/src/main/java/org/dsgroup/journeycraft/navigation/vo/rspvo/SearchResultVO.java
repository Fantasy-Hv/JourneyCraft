package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultVO {

    private List<ScenicSearchItemVO> scenicResults;

    private List<NodeSearchItemVO> nodeResults;

    private int total;

    private int page;

    private int size;
}
