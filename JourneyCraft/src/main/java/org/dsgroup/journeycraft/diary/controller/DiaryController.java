package org.dsgroup.journeycraft.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.diary.dto.DiaryCreateDTO;
import org.dsgroup.journeycraft.diary.service.DiaryService;
import org.dsgroup.journeycraft.diary.vo.reqvo.DiaryCreateReqVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryCreateRspVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
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
