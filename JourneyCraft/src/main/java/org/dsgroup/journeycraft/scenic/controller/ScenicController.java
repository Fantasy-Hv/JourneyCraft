package org.dsgroup.journeycraft.scenic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.dsgroup.journeycraft.scenic.vo.reqvo.BuildingListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FacilityListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FoodPlaceListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ReportCrowdReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicSearchReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.BuildingRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.FacilityRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.FoodPlaceRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicItemRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicListRspVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 景点模块 HTTP 接口。
 */
@RestController
@RequestMapping("/api/scenic")
@RequiredArgsConstructor
public class ScenicController {

    private final ScenicService scenicService;

    /**
     * 景点列表接口。
     */
    @GetMapping("/list")
    public Response<ScenicListRspVO> list(ScenicListReqVO reqVO) {
        return Response.ok(scenicService.listScenic(reqVO));
    }

    /**
     * 景点搜索接口。
     */
    @GetMapping("/search")
    public Response<ScenicListRspVO> search(@Valid ScenicSearchReqVO reqVO) {
        return Response.ok(scenicService.searchScenic(reqVO));
    }

    /**
     * 景点详情接口。
     */
    @GetMapping("/{id}")
    public Response<ScenicItemRspVO> detail(@PathVariable("id") Long id) {
        return Response.ok(scenicService.getScenicDetail(id));
    }

    /**
     * 建筑列表接口。
     */
    @GetMapping("/{id}/buildings")
    public Response<List<BuildingRspVO>> buildings(@PathVariable("id") Long id, BuildingListReqVO reqVO) {
        return Response.ok(scenicService.listBuildings(id, reqVO));
    }

    /**
     * 建筑详情接口。
     */
    @GetMapping("/buildings/{id}")
    public Response<BuildingRspVO> buildingDetail(@PathVariable("id") Long id) {
        return Response.ok(scenicService.getBuildingDetail(id));
    }

    /**
     * 设施列表接口。
     */
    @GetMapping("/{id}/facilities")
    public Response<List<FacilityRspVO>> facilities(@PathVariable("id") Long id, FacilityListReqVO reqVO) {
        return Response.ok(scenicService.listFacilities(id, reqVO));
    }

    /**
     * 设施详情接口。
     */
    @GetMapping("/facilities/{id}")
    public Response<FacilityRspVO> facilityDetail(@PathVariable("id") Long id) {
        return Response.ok(scenicService.getFacilityDetail(id));
    }

    /**
     * 美食列表接口。
     */
    @GetMapping("/{id}/foods")
    public Response<List<FoodPlaceRspVO>> foods(@PathVariable("id") Long id, FoodPlaceListReqVO reqVO) {
        return Response.ok(scenicService.listFoods(id, reqVO));
    }

    /**
     * 美食详情接口。
     */
    @GetMapping("/foods/{id}")
    public Response<FoodPlaceRspVO> foodDetail(@PathVariable("id") Long id) {
        return Response.ok(scenicService.getFoodDetail(id));
    }

    /**
     * 拥挤度上报接口。
     */
    @PostMapping("/{id}/report-crowd")
    public Response<Void> reportCrowd(@PathVariable("id") Long id, @Valid @RequestBody ReportCrowdReqVO reqVO) {
        scenicService.reportCrowd(id, reqVO);
        return Response.ok();
    }
}
