package org.dsgroup.journeycraft.scenic.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dsgroup.journeycraft.scenic.entity.Building;
import org.dsgroup.journeycraft.scenic.entity.CrowdLevel;
import org.dsgroup.journeycraft.scenic.entity.Facility;
import org.dsgroup.journeycraft.scenic.entity.FoodPlace;
import org.dsgroup.journeycraft.scenic.entity.ScenicArea;
import org.dsgroup.journeycraft.scenic.mapper.BuildingMapper;
import org.dsgroup.journeycraft.scenic.mapper.CrowdLevelMapper;
import org.dsgroup.journeycraft.scenic.mapper.FacilityMapper;
import org.dsgroup.journeycraft.scenic.mapper.FoodPlaceMapper;
import org.dsgroup.journeycraft.scenic.mapper.ScenicAreaMapper;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FoodPlaceListReqVO;
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
    private FoodPlaceMapper foodPlaceMapper;

    @Mock
    private CrowdLevelMapper crowdLevelMapper;

    private ScenicServiceImpl scenicService;

    @BeforeEach
    void setUp() {
        scenicService = new ScenicServiceImpl(
                scenicAreaMapper,
                buildingMapper,
                facilityMapper,
                foodPlaceMapper,
                crowdLevelMapper,
                new ObjectMapper()
        );
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

    @Test
    void getBuildingDetailShouldReturnTags() {
        Building building = new Building();
        building.setId(11L);
        building.setScenicAreaId(1L);
        building.setNodeId(22L);
        building.setName("building");
        building.setTags("[\"历史\",\"古风\"]");
        building.setImages("[\"img1\"]");
        when(buildingMapper.selectById(11L)).thenReturn(building);

        assertEquals(11L, scenicService.getBuildingDetail(11L).getId());
        assertEquals(2, scenicService.getBuildingDetail(11L).getTags().size());
    }

    @Test
    void getFacilityDetailShouldReturnTags() {
        Facility facility = new Facility();
        facility.setId(21L);
        facility.setScenicAreaId(1L);
        facility.setBuildingId(11L);
        facility.setNodeId(33L);
        facility.setName("facility");
        facility.setTags("[\"服务\"]");
        when(facilityMapper.selectById(21L)).thenReturn(facility);

        assertEquals(33L, scenicService.getFacilityDetail(21L).getNodeId());
    }

    @Test
    void getFoodDetailShouldReturnMappedFields() {
        FoodPlace foodPlace = new FoodPlace();
        foodPlace.setId(31L);
        foodPlace.setScenicAreaId(1L);
        foodPlace.setBuildingId(11L);
        foodPlace.setNodeId(44L);
        foodPlace.setName("food");
        foodPlace.setTags("[\"老字号\",\"排队\"]");
        when(foodPlaceMapper.selectById(31L)).thenReturn(foodPlace);

        assertEquals(44L, scenicService.getFoodDetail(31L).getNodeId());
    }

    @Test
    void listFoodsShouldFilterByBuildingId() {
        FoodPlace foodPlace = new FoodPlace();
        foodPlace.setId(41L);
        foodPlace.setScenicAreaId(1L);
        foodPlace.setBuildingId(11L);
        foodPlace.setName("food");
        when(foodPlaceMapper.selectList(any())).thenReturn(List.of(foodPlace));

        FoodPlaceListReqVO reqVO = new FoodPlaceListReqVO();
        reqVO.setBuildingId(11L);

        assertEquals(41L, scenicService.listFoods(1L, reqVO).get(0).getId());
    }
}
