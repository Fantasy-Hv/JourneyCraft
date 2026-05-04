package org.dsgroup.journeycraft.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.diary.dto.DiaryCreateDTO;
import org.dsgroup.journeycraft.diary.service.DiaryService;
import org.dsgroup.journeycraft.diary.vo.reqvo.CommentCreateReqVO;
import org.dsgroup.journeycraft.diary.vo.reqvo.DiaryCreateReqVO;
import org.dsgroup.journeycraft.diary.vo.reqvo.DiaryListReqVO;
import org.dsgroup.journeycraft.diary.vo.reqvo.DiaryUpdateReqVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.*;
import org.springframework.web.bind.annotation.*;

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
     * @param currentUserId 当前登录用户 ID
     * @return 创建的日记 ID
     */
    @PostMapping
    @Operation(summary = "创建日记", description = "创建一篇新的日记")
    public Response<DiaryCreateRspVO> createDiary(@RequestBody @Valid DiaryCreateReqVO reqVO,
                                                  @RequestAttribute("currentUserId") Long currentUserId) {
        // VO 转 DTO
        DiaryCreateDTO dto = convertToDTO(reqVO);

        // 调用 Service 层
        String diaryId = diaryService.createDiary(dto, currentUserId);

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
        result.put("total", list.size());

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
     * 更新日记
     * @param id 日记 ID
     * @param reqVO 更新请求 VO
     * @param currentUserId 当前登录用户 ID
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新日记", description = "更新日记信息，仅作者可修改")
    public Response updateDiary(@PathVariable String id, @RequestBody @Valid DiaryUpdateReqVO reqVO,
                                @RequestAttribute("currentUserId") Long currentUserId) {
        DiaryCreateDTO dto = convertToUpdateDTO(reqVO);
        diaryService.updateDiary(id, dto, currentUserId);
        return Response.ok();
    }

    /**
     * 删除日记
     * @param id 日记 ID
     * @param currentUserId 当前登录用户 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除日记", description = "删除日记，仅作者可删除")
    public Response deleteDiary(@PathVariable String id,
                                @RequestAttribute("currentUserId") Long currentUserId) {
        diaryService.deleteDiary(id, currentUserId);
        return Response.ok();
    }

    /**
     * 点赞日记
     * @param id 日记 ID
     * @param currentUserId 当前登录用户 ID
     * @return 点赞结果
     */
    @PostMapping("/{id}/like")
    @Operation(summary = "点赞日记", description = "点赞/取消点赞日记")
    public Response<DiaryLikeRspVO> likeDiary(@PathVariable String id,
                                              @RequestAttribute("currentUserId") Long currentUserId) {
        DiaryLikeRspVO result = diaryService.toggleLike(id, currentUserId);
        return Response.ok(result);
    }

    /**
     * 添加评论
     * @param id 日记 ID
     * @param reqVO 评论请求 VO
     * @param currentUserId 当前登录用户 ID
     * @return 评论 ID
     */
    @PostMapping("/{id}/comment")
    @Operation(summary = "添加评论", description = "为日记添加评论或回复评论")
    public Response<Map<String, String>> addComment(@PathVariable String id, @RequestBody @Valid CommentCreateReqVO reqVO,
                                                    @RequestAttribute("currentUserId") Long currentUserId) {
        String commentId = diaryService.addComment(id, reqVO, currentUserId);
        Map<String, String> result = new HashMap<>();
        result.put("commentId", commentId);
        return Response.ok(result);
    }

    /**
     * 查询评论列表
     * @param id 日记 ID
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    @GetMapping("/{id}/comments")
    @Operation(summary = "查询评论列表", description = "查询日记的评论列表")
    public Response<Map<String, Object>> getCommentList(@PathVariable String id,
                                                        @RequestParam(required = false, defaultValue = "1") Integer page,
                                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<CommentRspVO> list = diaryService.getCommentList(id, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Response.ok(result);
    }

    /**
     * 删除评论
     * @param id 评论 ID
     * @return 操作结果
     */
    @DeleteMapping("/comment/{id}")
    @Operation(summary = "删除评论", description = "删除评论，仅评论发布者或日记作者可删除")
    public Response deleteComment(@PathVariable String id,
                                  @RequestAttribute("currentUserId") Long currentUserId) {
        diaryService.deleteComment(id, currentUserId);
        return Response.ok();
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
     * 将更新 VO 转换为 DTO
     */
    private DiaryCreateDTO convertToUpdateDTO(DiaryUpdateReqVO reqVO) {
        return DiaryCreateDTO.builder()
                .title(reqVO.getTitle())
                .content(reqVO.getContent())
                .scenicAreaId(reqVO.getScenicAreaId())
                .tripId(reqVO.getTripId())
                .coverImage(reqVO.getCoverImage())
                .images(reqVO.getImages())
                .videos(reqVO.getVideos())
                .path(convertToUpdatePathNodeDTOs(reqVO.getPath()))
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

    /**
     * 将更新 VO 的路径节点转换为 DTO
     */
    private java.util.List<DiaryCreateDTO.PathNodeDTO> convertToUpdatePathNodeDTOs(
            java.util.List<DiaryUpdateReqVO.PathNodeReqVO> pathNodeReqVOS) {
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
