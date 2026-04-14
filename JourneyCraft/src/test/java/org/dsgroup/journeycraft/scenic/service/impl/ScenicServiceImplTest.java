package org.dsgroup.journeycraft.scenic.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dsgroup.journeycraft.scenic.entity.Building;
import org.dsgroup.journeycraft.scenic.entity.CrowdLevel;
import org.dsgroup.journeycraft.scenic.entity.Facility;
import org.dsgroup.journeycraft.scenic.entity.ScenicArea;
import org.dsgroup.journeycraft.scenic.mapper.BuildingMapper;
import org.dsgroup.journeycraft.scenic.mapper.CrowdLevelMapper;
import org.dsgroup.journeycraft.scenic.mapper.FacilityMapper;
import org.dsgroup.journeycraft.scenic.mapper.ScenicAreaMapper;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ReportCrowdReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicListReqVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenicServiceImplTest {

    @Mock
    private ScenicAreaMapper scenicAreaMapper;

    @Mock
    private BuildingMapper buildingMapper;

    @Mock
    private FacilityMapper facilityMapper;

    @Mock
    private CrowdLevelMapper crowdLevelMapper;

    private ScenicServiceImpl scenicService;

    @BeforeEach
    void setUp() {
        scenicService = new ScenicServiceImpl(scenicAreaMapper, buildingMapper, facilityMapper, crowdLevelMapper, new ObjectMapper());
    }

    @Test
    void listScenicShouldSortByDistance() {
        ScenicArea far = new ScenicArea();
        far.setId(2L);
        far.setName("far");
        far.setLatitude(BigDecimal.valueOf(1.0));
        far.setLongitude(BigDecimal.valueOf(1.0));

        ScenicArea near = new ScenicArea();
        near.setId(1L);
        near.setName("near");
        near.setLatitude(BigDecimal.valueOf(0.01));
        near.setLongitude(BigDecimal.valueOf(0.01));

        when(scenicAreaMapper.selectList(any())).thenReturn(List.of(far, near));

        ScenicListReqVO reqVO = new ScenicListReqVO();
        reqVO.setPage(1);
        reqVO.setSize(1);
        reqVO.setSortBy("distance");
        reqVO.setSortOrder("asc");
        reqVO.setLatitude(0.0);
        reqVO.setLongitude(0.0);

        assertEquals(1L, scenicService.listScenic(reqVO).getList().get(0).getId());
    }

    @Test
    void reportCrowdShouldInsertRecord() {
        ScenicArea scenicArea = new ScenicArea();
        scenicArea.setId(1L);
        scenicArea.setName("scenic");
        when(scenicAreaMapper.selectById(1L)).thenReturn(scenicArea);

        ReportCrowdReqVO reqVO = new ReportCrowdReqVO();
        reqVO.setNodeId(10L);
        reqVO.setLevel(2);
        reqVO.setCrowdCount(100);

        scenicService.reportCrowd(1L, reqVO);

        ArgumentCaptor<CrowdLevel> captor = ArgumentCaptor.forClass(CrowdLevel.class);
        verify(crowdLevelMapper).insert(captor.capture());
        assertEquals(1L, captor.getValue().getScenicAreaId());
        assertEquals(10L, captor.getValue().getNodeId());
        assertEquals(2, captor.getValue().getLevel());
    }
}
