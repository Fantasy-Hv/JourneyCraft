package org.dsgroup.journeycraft.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.diary.dto.DiaryCreateDTO;
import org.dsgroup.journeycraft.diary.service.DiaryService;
import org.dsgroup.journeycraft.diary.vo.reqvo.DiaryCreateReqVO;
import org.dsgroup.journeycraft.diary.vo.reqvo.DiaryListReqVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryCreateRspVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryDetailRspVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryListRspVO;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日记控制器
 */
@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
@Tag(name = "日记管理", description = "日记创建、查询、更新、删除等接口")
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 创建日记
     * @param reqVO 创建日记请求 VO
     * @return 创建的日记 ID
     */
    @PostMapping
    @Operation(summary = "创建日记", description = "创建一篇新的日记，需要携带 Token 进行认证")
    public Response<DiaryCreateRspVO> createDiary(@RequestBody @Valid DiaryCreateReqVO reqVO) {
        // VO 转 DTO
        DiaryCreateDTO dto = convertToDTO(reqVO);

        // 调用 Service 层
        String diaryId = diaryService.createDiary(dto);

        // 返回响应
        DiaryCreateRspVO rspVO = DiaryCreateRspVO.builder()
                .diaryId(diaryId)
                .build();

        return Response.ok(rspVO);
    }

    /**
     * 查询日记列表
     * @param reqVO 查询请求 VO
     * @return 日记列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询日记列表", description = "分页查询日记列表，支持按用户、标签、排序等条件过滤")
    public Response<Map<String, Object>> getDiaryList(DiaryListReqVO reqVO) {
        List<DiaryListRspVO> list = diaryService.getDiaryList(
                reqVO.getUserId(),
                reqVO.getTags(),
                reqVO.getSortBy(),
                reqVO.getPage(),
                reqVO.getSize()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size()); // TODO: 实际需要查询总数

        return Response.ok(result);
    }

    /**
     * 查询日记详情
     * @param id 日记 ID
     * @return 日记详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询日记详情", description = "根据日记 ID 查询完整日记信息")
    public Response<DiaryDetailRspVO> getDiaryDetail(@PathVariable String id) {
        DiaryDetailRspVO detail = diaryService.getDiaryDetail(id);
        return Response.ok(detail);
    }

    /**
     * 将 VO 转换为 DTO
     */
    private DiaryCreateDTO convertToDTO(DiaryCreateReqVO reqVO) {
        return DiaryCreateDTO.builder()
                .title(reqVO.getTitle())
                .content(reqVO.getContent())
                .scenicAreaId(reqVO.getScenicAreaId())
                .tripId(reqVO.getTripId())
                .coverImage(reqVO.getCoverImage())
                .images(reqVO.getImages())
                .videos(reqVO.getVideos())
                .path(convertToPathNodeDTOs(reqVO.getPath()))
                .tags(reqVO.getTags())
                .rating(reqVO.getRating())
                .mood(reqVO.getMood())
                .weather(reqVO.getWeather())
                .companions(reqVO.getCompanions())
                .status(reqVO.getStatus())
                .build();
    }

    /**
     * 将 VO 的路径节点转换为 DTO
     */
    private java.util.List<DiaryCreateDTO.PathNodeDTO> convertToPathNodeDTOs(
            java.util.List<DiaryCreateReqVO.PathNodeReqVO> pathNodeReqVOS) {
        if (pathNodeReqVOS == null || pathNodeReqVOS.isEmpty()) {
            return null;
        }

        return pathNodeReqVOS.stream()
                .map(req -> DiaryCreateDTO.PathNodeDTO.builder()
                        .nodeName(req.getNodeName())
                        .nodeId(req.getNodeId())
                        .timestamp(req.getTimestamp())
                        .lat(req.getLat())
                        .lng(req.getLng())
                        .photoCount(req.getPhotoCount())
                        .images(req.getImages())
                        .videos(req.getVideos())
                        .build())
                .collect(Collectors.toList());
    }
}
